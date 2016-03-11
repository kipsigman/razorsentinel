$(document).ready(function() {
  $(saveButtonId).click(function() {
    event.preventDefault();
    console.log("saveHandler");

    var saveArticleForm = $('#save-article-form');
    
    // Add hidden inputs for tags
    var tagData = getTagData();
    for (var tagKey in tagData) {
      var tagValue = tagData[tagKey];
      saveArticleForm.append('<input type="hidden" name="' + tagKey + '" value="' + tagValue + '" />');
    }
    
    saveArticleForm.submit();
  });
  
  // Initialize tags
  initializeTags(true);
});