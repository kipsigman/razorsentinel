// JS for across site usage
$(document).ready(function() {
  $(".action-delete").click(function() {
    event.preventDefault();
    var choice = confirm(Messages('action.delete.confirm'));
    if (choice) {
      window.location.href = this.getAttribute('href');
    }
  });
});