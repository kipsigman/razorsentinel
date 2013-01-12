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
        success: function(response, newValue) {
            // Update all other tags of the same name
            var tagName = $(this)[0].getAttribute('data-name');
            var tagValue = newValue;
            var tags = $('[data-name="' + tagName + '"]');
            tags.html(tagValue);
            
            // Show sharing info if article is published
            // $('#articleId').val(data.articleId);
            if (response.status == "PUBLISH") {
                // Set article URL in div
                $('#articleUrl')[0].value = response.url;
                $('#shareArticleDraft').hide();
                $('#shareArticlePublish').show();
            }
        }
    });
    $('#shareArticleDraft').show();
    $('#shareArticlePublish').hide();
});