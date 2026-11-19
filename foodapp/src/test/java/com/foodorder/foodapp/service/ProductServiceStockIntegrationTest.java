package com.foodorder.foodapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.foodorder.foodapp.dto.product.CreateProductDTO;
import com.foodorder.foodapp.dto.product.UpdateProductDTO;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.repository.CategoryRepository;
import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.repository.ProductTypeRepository;
import com.foodorder.foodapp.support.IntegrationTest;

/**
 * Stock handling of {@link ProductService} (issue #29): the admin edit form must never write
 * stock back, and restock adds to the current stock.
 */
@IntegrationTest
class ProductServiceStockIntegrationTest {

  @Autowired
  private ProductService productService;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ProductTypeRepository productTypeRepository;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private PlatformTransactionManager transactionManager;

  /** Spy: lets a test change the stock at a precise moment inside updateProduct. */
  @MockitoSpyBean
  private ImageUploadService imageUploadService;

  private final List<Long> createdProductIds = new ArrayList<>();

  @AfterEach
  void deleteCreatedProducts() {
    productRepository.deleteAllById(createdProductIds);
  }

  /**
   * The stock changes (e.g. a customer orders) after updateProduct has loaded the product and
   * before it saves. The stock must keep that new value. Guards both halves of the fix: without
   * @Transactional (save merges a detached copy) or without @DynamicUpdate (UPDATE writes every
   * column), the stock goes back to the 100 read at the start.
   */
  @Test
  void updateProduct_changePrice_keepsStockChangedDuringUpdate() {
    Long productId = createProduct(100);
    changeStockDuringUpdate(productId, 97);

    UpdateProductDTO dto = updateDtoWithCurrentValues(productId);
    dto.setPrice(46_000);
    productService.updateProduct(dto);

    Product saved = productRepository.findById(productId).orElseThrow();
    assertThat(saved.getQuantity()).isEqualTo(97);
    assertThat(saved.getPrice()).isEqualTo(46_000);
  }

  /** Same race, the admin renames the product instead: the stock keeps its new value. */
  @Test
  void updateProduct_rename_keepsStockChangedDuringUpdate() {
    Long productId = createProduct(100);
    changeStockDuringUpdate(productId, 97);

    UpdateProductDTO dto = updateDtoWithCurrentValues(productId);
    dto.setName("Renamed stock test product");
    productService.updateProduct(dto);

    Product saved = productRepository.findById(productId).orElseThrow();
    assertThat(saved.getQuantity()).isEqualTo(97);
    assertThat(saved.getName()).isEqualTo("Renamed stock test product");
  }

  @Test
  void restockProduct_addsAmountToLatestStock() {
    Long productId = createProduct(100);
    setStockFromAnotherRequest(productId, 97); // e.g. a customer ordered 3 after the page opened

    productService.restockProduct(productId, 20);

    assertThat(productRepository.findById(productId).orElseThrow().getQuantity()).isEqualTo(117);
  }

  @Test
  void restockProduct_rejectsDeletedProduct() {
    Long productId = createProduct(100);
    productService.deleteProduct(productId); // soft delete

    assertThatThrownBy(() -> productService.restockProduct(productId, 20))
        .isInstanceOf(ResourceNotFoundException.class);
    assertThat(productRepository.findById(productId).orElseThrow().getQuantity()).isEqualTo(100);
  }

  // ---- test data (category and product type come from the Flyway master data) ----

  private Long createProduct(int quantity) {
    CreateProductDTO dto = new CreateProductDTO();
    dto.setName("Stock test product");
    dto.setPrice(45_000);
    dto.setQuantity(quantity);
    dto.setIsActive(true);
    dto.setCategoryId(categoryRepository.findAll().get(0).getId());
    dto.setProductTypeId(productTypeRepository.findAll().get(0).getId());

    Long id = productService.createProduct(dto).getId();
    createdProductIds.add(id);
    return id;
  }

  /** The edit form as the admin opens it (no quantity field); each test changes one field. */
  private UpdateProductDTO updateDtoWithCurrentValues(Long productId) {
    Product current = productRepository.findById(productId).orElseThrow();
    UpdateProductDTO dto = new UpdateProductDTO();
    dto.setId(productId);
    dto.setName(current.getName());
    dto.setPrice(current.getPrice());
    dto.setIsActive(current.getIsActive());
    dto.setCategoryId(current.getCategory().getId());
    dto.setProductTypeId(current.getProductType().getId());
    return dto;
  }

  /**
   * Sets the stock to {@code quantity} while updateProduct runs: uploadImage is called after
   * findById and before save, so the change commits exactly between the two.
   */
  private void changeStockDuringUpdate(Long productId, int quantity) {
    doAnswer(invocation -> {
      setStockFromAnotherRequest(productId, quantity);
      return invocation.callRealMethod();
    }).when(imageUploadService).uploadImage(any());
  }

  /**
   * Sets the stock with plain SQL in its own committed transaction, as another request (e.g. a
   * customer's order) would. Stands in for any concurrent stock change: no real order is created.
   */
  private void setStockFromAnotherRequest(Long productId, int quantity) {
    TransactionTemplate tx = new TransactionTemplate(transactionManager);
    tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    tx.executeWithoutResult(status ->
        jdbcTemplate.update("UPDATE products SET quantity = ? WHERE id = ?", quantity, productId));
  }
}
