// Objects
var contextKey = {
  error : "error",
  info : "info",
  success : "success",
  warning : "warning"
};

//Functions
function isScrolledIntoView(elem) {
  var docViewTop = $(window).scrollTop();
  var docViewBottom = docViewTop + $(window).height();
  var elemTop = $(elem).offset().top;
  var elemBottom = elemTop + $(elem).height();
  return ((elemBottom >= docViewTop) && (elemTop <= docViewBottom) && (elemBottom <= docViewBottom) && (elemTop >= docViewTop));
}

function scrollToById(cssId) {
  $('html,body').animate({scrollTop: $(cssId).offset().top},'slow');
}

function scrollToByElement(el) {
  $('html,body').animate({scrollTop: el.offset().top},'slow');
}

// Handlers
$(".action-delete").click(function() {
  event.preventDefault();
  var choice = confirm(messages('action.delete.confirm'));
  if (choice) {
    window.location.href = this.getAttribute('href');
  }
});