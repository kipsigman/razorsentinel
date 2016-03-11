function hideModal(id) {
  var theModal = $('#' + id);
  theModal.modal('hide');
}

function showModal(id, context, title, body) {
  var theModal = $('#' + id);

  // Set alert class
  var modalContent = theModal.find('.modal-content');
  if (context === contextKey.error) {
    modalContent.addClass("alert").addClass("alert-danger");
  } else if (context === contextKey.info) {
    modalContent.addClass("alert").addClass("alert-info");
  } else if (context === contextKey.success) {
    modalContent.addClass("alert").addClass("alert-success");
  } else if (context === contextKey.warning) {
    modalContent.addClass("alert").addClass("alert-warning");
  }

  // Populate content
  theModal.find('.modal-title').text(title);
  theModal.find('.modal-body').text(body);

  // show
  theModal.modal('show');
}