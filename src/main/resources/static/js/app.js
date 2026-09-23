document.querySelectorAll('[data-auto-submit]').forEach(el => el.addEventListener('change', () => el.form.requestSubmit()));
document.querySelectorAll('.languages a').forEach(link => {
  const target = new URL(location.href);
  target.searchParams.set('lang', new URL(link.href).searchParams.get('lang'));
  link.href = target.pathname + target.search;
});
document.querySelectorAll('form[method="post"]').forEach(form => form.addEventListener('submit', () => {
  const button = form.querySelector('button');
  if (button) { button.disabled = true; button.classList.add('busy'); }
}));
document.querySelectorAll('nav a').forEach(a => {
  if (new URL(a.href).pathname === location.pathname) a.setAttribute('aria-current', 'page');
});

