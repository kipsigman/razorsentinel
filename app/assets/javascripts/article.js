$(document).ready(function() {
    $('.field-editable').editable({
        placement: 'bottom',
        pk: $('#articleId')[0].value,
        params: {articleTemplateId:$('#articleTemplateId')[0].value},
        url: '/articles/updateTag',
        validate: function(value) {
            if($.trim(value) == '') 
                return 'This field is required';
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if(jqXHR.status == 500){
                return 'Internal server error'
            } else if(xhr.status == 401) {
                return 'Your session has expired. Please log in again.';
            }
        },
        success: function(data, textStatus, jqXHR) {
            // Update all other tags of the same name
            var tagName = $(this)[0].getAttribute('data-name');
            var tagValue = textStatus;
            var tags = $('[data-name="' + tagName + '"]');
            tags.html(tagValue);
            
            // Show sharing info if article is published
            // $('#articleId').val(data.articleId);
            if (data.status == "PUBLISH") {
                // Set article URL in div
                $('#articleUrl')[0].value = data.url;
                $('#shareArticleDraft').hide();
                $('#shareArticlePublish').show();
            }
        }
    });
    $('#shareArticleDraft').show();
    $('#shareArticlePublish').hide();
});