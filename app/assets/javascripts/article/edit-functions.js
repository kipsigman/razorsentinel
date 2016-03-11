var articleTagClass = ".article-tag-editable";
var saveButtonId = "#save-btn";

function getArticleId() {
  return $('#articleId')[0].value;
}

function getArticleTemplateId() {
  return $('#articleTemplateId')[0].value;
}

function checkEnableSave() {
  var allTagsSet = true;
  $(articleTagClass).each(function() {
    if ($(this).data("value-set") === "false") {
      allTagsSet = false;
    }
  });
  if (allTagsSet) {
    $(saveButtonId).removeAttr("disabled");
  }
}

function getTagData() {
  var tagData = {};
  
  $(articleTagClass).each(function() {
    var key = $(this).data("name");
    var value = $(this).text();
    tagData[key] = value;
  });
  
  return tagData;
}

function getFormData() {
  // Get tag data
  var formData = getTagData();
  
  // Get other form data
  formData.articleId = getArticleId();
  formData.articleTemplateId = getArticleTemplateId();
  formData.publishDate = $('#publishDate')[0].value;
  formData.publishDateFixed = $('input[name=publishDateFixed]:checked').val();
  
  return formData;
}

function updateTags(name, newValue) {
  var tags = $('[data-name="' + name + '"]');
  tags.html(newValue);
  tags.editable('option', 'value', newValue);
  tags.data("value-set", "true");
}

function initializeTags(isNew) {
  //Initialize tags for checkEnableSave
  if (isNew) {
    $(articleTagClass).data("value-set", "false");
  } else {
    $(articleTagClass).data("value-set", "true");  
  }
  
  // Initialize inline editing
  $(articleTagClass).editable({
    type : 'text',
    placement : 'bottom',
    inputclass : 'input-large',
    //pk : getArticleId(), // Setting PK enables post for each tag edit 
    url : routes.controllers.ArticleController.saveTag().url,
    validate : function(value) {
      if ($.trim(value) === '')
        return 'This field is required';
    },
    success : function(response, newValue) {
      var tagName = $(this)[0].getAttribute('data-name');
      updateTags(tagName, newValue);
      
      checkEnableSave();
    }
  });
}