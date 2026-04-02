/* Gestor de Estoques – shared sidebar / mobile toggle logic */
(function () {
  var MOBILE_BREAKPOINT = 768;

  var menuToggle = document.getElementById('menuToggle');
  var sidebar    = document.getElementById('sidebar');

  if (!menuToggle || !sidebar) return;

  function applyLayout() {
    if (window.innerWidth <= MOBILE_BREAKPOINT) {
      menuToggle.style.display = 'block';
    } else {
      menuToggle.style.display = 'none';
      sidebar.classList.remove('open');
      menuToggle.setAttribute('aria-expanded', 'false');
    }
  }

  menuToggle.addEventListener('click', function () {
    var isOpen = sidebar.classList.toggle('open');
    menuToggle.setAttribute('aria-expanded', String(isOpen));
  });

  window.addEventListener('resize', applyLayout);
  applyLayout();
}());
