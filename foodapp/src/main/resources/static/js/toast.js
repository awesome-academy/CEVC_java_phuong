document.addEventListener('DOMContentLoaded', function () {
  const toastEl = document.getElementById('success-toast');
  if (toastEl) {
    const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
    toast.show();
  }
});

document.addEventListener('DOMContentLoaded', function () {
  const toastEl = document.getElementById('failed-toast');
  if (toastEl) {
    const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
    toast.show();
  }
});
