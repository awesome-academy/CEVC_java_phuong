document.addEventListener('DOMContentLoaded', function () {
  const toggleButton = document.querySelector('#group-show-pw #btn-show-pw');
  const icon = document.querySelector('#group-show-pw i');

  if (toggleButton) {
    toggleButton.addEventListener('click', function () {
      const pwField = document.querySelector(
        "#group-show-pw input[type='password'], #group-show-pw input[type='text']"
      );

      const pwFieldType = pwField.getAttribute('type');
      if (pwFieldType === 'password') {
        pwField.setAttribute('type', 'text');
        icon.classList.add('bi-eye-slash-fill');
        icon.classList.remove('bi-eye-slash');
      } else {
        pwField.setAttribute('type', 'password');
        icon.classList.add('bi-eye-slash');
        icon.classList.remove('bi-eye-slash-fill');
      }
    });
  }
});
