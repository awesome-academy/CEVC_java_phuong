const CANCELED_STATUS_ID = '5';

function toggleReasonCancelVisibility() {
  var selectElement = document.getElementById('statusSelect');
  var noteDiv = document.getElementById('reasonCancelDiv');

  if (selectElement.value === CANCELED_STATUS_ID) {
    noteDiv.classList.remove('d-none');
  } else {
    noteDiv.classList.add('d-none');
  }
}

var updateStatusModal = document.getElementById('updateStatusModal');

updateStatusModal.addEventListener('show.bs.modal', function (event) {
  var button = event.relatedTarget;
  var orderId = button.getAttribute('data-order-id');
  var currentStatusId = button.getAttribute('data-current-status-id');
  var orderReasonCancel = button.getAttribute('data-order-reason-cancel');

  // Update title modal
  var modalTitle = updateStatusModal.querySelector('#updateStatusModalLabel');
  modalTitle.textContent = 'Update Status for order id #' + orderId;

  // Update form action URL
  var form = document.getElementById('updateStatusForm');
  form.action = '/admin/orders/' + orderId + '/status';

  // Update selected status in dropdown
  var selectElement = document.getElementById('statusSelect');
  selectElement.value = currentStatusId;

  // Update reason cancel textarea
  var reasonCancelTextarea = document.getElementById('reasonCancelTextarea');
  reasonCancelTextarea.value = orderReasonCancel;

  if (!selectElement.hasAttribute('data-listener-added')) {
    selectElement.addEventListener('change', toggleReasonCancelVisibility);
    selectElement.setAttribute('data-listener-added', 'true');
  }

  toggleReasonCancelVisibility();
});

updateStatusModal.addEventListener('hidden.bs.modal', function () {
  document.getElementById('reasonCancelDiv').classList.add('d-none');
  document.getElementById('reasonCancelTextarea').value = '';
});
