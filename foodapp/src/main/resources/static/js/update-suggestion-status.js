const REJECTED_STATUS = 'REJECTED';

var updateStatusModal = document.getElementById('updateStatusModal');
var statusSelect = document.getElementById('statusSelect');
var reasonRejectDiv = document.getElementById('reasonRejectDiv');
var reasonRejectTextarea = document.getElementById('reasonRejectTextarea');

// Reason reject is only shown (and required) when REJECTED is selected
function toggleReasonRejectVisibility() {
  var isRejected = statusSelect.value === REJECTED_STATUS;
  reasonRejectDiv.classList.toggle('d-none', !isRejected);
  reasonRejectTextarea.required = isRejected;
}

statusSelect.addEventListener('change', toggleReasonRejectVisibility);

// Fill the modal with the clicked suggestion before it opens
updateStatusModal.addEventListener('show.bs.modal', function (event) {
  var button = event.relatedTarget;
  var suggestionId = button.getAttribute('data-suggestion-id');
  var currentStatus = button.getAttribute('data-current-status');

  updateStatusModal.querySelector('#updateStatusModalLabel').textContent =
    'Update Status for suggestion id #' + suggestionId;

  document.getElementById('updateStatusForm').action =
    '/admin/suggestions/' + suggestionId + '/update-status';

  // Hide the current status (status only moves forward) and select the first remaining option
  var firstVisible = null;
  Array.from(statusSelect.options).forEach(function (option) {
    option.hidden = option.value === currentStatus;
    if (!option.hidden && firstVisible === null) {
      firstVisible = option.value;
    }
  });
  statusSelect.value = firstVisible;

  toggleReasonRejectVisibility();
});

updateStatusModal.addEventListener('hidden.bs.modal', function () {
  reasonRejectTextarea.value = '';
});
