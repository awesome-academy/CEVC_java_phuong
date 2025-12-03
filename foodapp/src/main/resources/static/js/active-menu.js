document.addEventListener('DOMContentLoaded', function () {
  const navLinks = document.querySelectorAll('#navigation a.nav-link');
  const currentPath = window.location.pathname;

  navLinks.forEach((link) => {
    const linkPath = link.getAttribute('href');

    if (linkPath === currentPath) {
      navLinks.forEach((l) => l.classList.remove('active'));
      link.classList.add('active');
    }
  });
});
