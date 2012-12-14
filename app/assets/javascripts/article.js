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
            console.log($('#articleId'));
            $('#articleId').val(data.articleId);
            if (data.status == "PUBLISHED") {
                // Set article URL in div
                $('#articleUrl')[0].value = data.url;
                console.log(data.status,data.url);
                $('#shareArticleDraft').hide();
                $('#shareArticlePublished').show();
            }
        }
    });
    $('.field-editable').on('update', function(e, editable) {
        // Update all other tags of the same name
        var tagName = this.getAttribute('data-name');
        var tagValue = $(this).html();
        var tags = $('[data-name="' + tagName + '"]');
        tags.html(tagValue);
    });
    $('#shareArticleDraft').show();
    $('#shareArticlePublished').hide();
});