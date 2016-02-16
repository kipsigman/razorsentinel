$(document).ready(function() {
    function displayCanPublish(canPublish) {
      if (canPublish === true) {
        $('#canPublishFalse').hide();
        $('#canPublishTrue').show();
      } else {
        $('#canPublishFalse').show();
        $('#canPublishTrue').hide();
      }    
    }
    
    //  Initialize canPublish
    displayCanPublish(JSON.parse($('#canPublish')[0].value));
    
    var articleId = $('#articleId')[0].value;
    // Inline Tag editing
    $('.field-editable').editable({
        type: 'text',
        placement: 'bottom',
        inputclass: 'input-large',
        pk: articleId,
        //params: {articleTemplateId:$('#articleTemplateId')[0].value},
        url: jsRoutes.controllers.ArticleController.saveTag(articleId).url,
        validate: function(value) {
            if($.trim(value) === '') 
                return 'This field is required';
        },
        success: function(response, newValue) {
            // Update all other tags of the same name
            var tagName = $(this)[0].getAttribute('data-name');
            var tagValue = newValue;
            var tags = $('[data-name="' + tagName + '"]');
            tags.html(tagValue);
            
            displayCanPublish(response.canPublish);
        }
    });
});