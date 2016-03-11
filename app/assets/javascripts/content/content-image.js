$(document).ready(function() {
  var contentClasses = {article:"Article", articleTemplate:"ArticleTemplate"};
  var contentId = $('#contentId')[0].value;
  var contentClass = $('#contentClass')[0].value;
  
  function contentImageUrls(imageId) {
    var urls = {};
    if (contentClass === contentClasses.articleTemplate) {
        if(imageId) {
            urls.deleteUrl = routes.controllers.ArticleTemplateController.deleteContentImage(contentId, imageId).url;
            urls.saveUrl = routes.controllers.ArticleTemplateController.saveContentImage(contentId, imageId).url;
        } else {
            urls.deleteUrl = "";
            urls.saveUrl = routes.controllers.ArticleTemplateController.saveNewContentImage(contentId).url;    
        }
    } else if (contentClass === contentClasses.article) {
        if(imageId) {
            urls.deleteUrl = routes.controllers.ArticleController.deleteContentImage(contentId, imageId).url;
            urls.saveUrl = routes.controllers.ArticleController.saveContentImage(contentId, imageId).url;
        } else {
            urls.deleteUrl = "";
            urls.saveUrl = routes.controllers.ArticleController.saveNewContentImage(contentId).url;    
        }
    } else {
        alert("No valid contentClass defined for this page");
    }
    
    return urls;
  }
  
  function getFormData(imageContainer) {
    var contentId = $(imageContainer).data("content-id");
    var formData = {contentId:contentId};
    return formData;   
  }
  
  function createDropzone(imageContainer, imageId) {
    $.ajax({
        type : "GET",
        url : routes.controllers.ImageController.imageDropzone(imageId).url,
        dataType : "html",
        success: function(data, textStatus, jqXHR) {
           imageContainer.html(data);
           var dropzoneDiv = $(imageContainer).children('.dropzone');
           enableDropzone(imageContainer, dropzoneDiv, imageId);
       }
    });
  }
  
  function enableDropzone(imageContainer, imageDropzone, imageId) {
    var urls = contentImageUrls(imageId);
    
    $(imageDropzone).html5imageupload({
        data: getFormData(imageContainer),
        url: urls.saveUrl,
        removeurl: urls.deleteUrl,
        onAfterProcessImage: function(value) {
            if (!imageId) {
                var filename = imageDropzone.data("filename");
                var newImageId = parseInt(filename.substring(0, filename.indexOf(".")));
                
                createDropzone(imageContainer, newImageId);
            }
        }
    });
  }
  
  // Init dropzones
  $(".dropzone").each(function(index) {
    var imageDropzone = $(this);
    var imageContainer = imageDropzone.parent('.image-container');
    var imageId = imageContainer.data("image-id");
    enableDropzone(imageContainer, imageDropzone, imageId);
  });
});