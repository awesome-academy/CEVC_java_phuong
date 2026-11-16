var updateStatusModal = document.getElementById('updateStatusModal');

// Fill the modal with the clicked suggestion before it opens
updateStatusModal.addEventListener('show.bs.modal', function (event) {
  var button = event.relatedTarget;
  var suggestionId = button.getAttribute('data-suggestion-id');
  var currentStatus = button.getAttribute('data-current-status');

  updateStatusModal.querySelector('#updateStatusModalLabel').textContent =
    'Update Status for suggestion id #' + suggestionId;

  document.getElementById('updateStatusForm').action =
    '/admin/suggestions/' + suggestionId + '/update-status';

  document.getElementById('statusSelect').value = currentStatus;
});
