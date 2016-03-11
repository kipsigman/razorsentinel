$(document).ready(function() {
  function populateWidget(container, populateUrl, append, hasArticleImages) {
    $.ajax({
      type : "GET",
      url : populateUrl,
      dataType : "html",
      success : function(data, textStatus, jqXHR) {
        if (append) {
          container.append(data);
          if(hasArticleImages) {
            playhover();
          }
        } else {
          container.html(data);
        }
      }
    });
  }

  function populateWeather() {
    var container = $('.header-weather');
    if (container.length) {
      var populateUrl = routes.controllers.WidgetController.weather().url;
      populateWidget(container, populateUrl, false, false);
    }
  }

  function populateRecentArticles() {
    var container = $('#widget-recent-articles');
    if (container.length) {
      var populateUrl = container.data("populate-url");
      populateWidget(container, populateUrl, true, true);
    }
  }

  function populateTrendingArticles() {
    var container = $('#widget-trending-articles');
    if (container.length) {
      var populateUrl = container.data("populate-url");
      populateWidget(container, populateUrl, true, true);
    }
  }
  
  function initializeCalendar() {
    var container = $('#widget-calendar-fullcalendar');
    if(container) {
      container.fullCalendar({
        events: {
          googleCalendarId: 'en.usa#holiday@group.v.calendar.google.com'
        },
        fixedWeekCount: false,
        googleCalendarApiKey: config.google.apiKey,
        header: {
          left: 'prev',
          center: 'title',
          right: 'next'
        },
        height: 'auto'
      });
    }
  }

  populateWeather();
  initializeCalendar();
  populateRecentArticles();
  populateTrendingArticles();
  
});