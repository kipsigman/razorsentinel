$(document).ready(function() {
  function replaceBody(newBody) {
    $("#body").val(newBody);
  }
  $("#tidy-body").click(function() {
    event.preventDefault();
    var content = $("#tidy-body-content").text();
    replaceBody(content);
  });
});