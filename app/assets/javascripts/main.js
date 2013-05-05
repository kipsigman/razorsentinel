var NEWS = NEWS || {};
NEWS.forms = {
    /**
     * Function to populate options for a secondary select depending on the primary value.
     */
    dependentSelect: function(primarySelector, secondarySelector, getUrl) {
        "use strict";
        var primarySelect = $(primarySelector);
        var secondarySelect = $(secondarySelector);
        primarySelect.change(function() {
            var primarySelectVal = primarySelect.val();
            if (primarySelectVal && primarySelectVal !== "") {
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: getUrl(primarySelectVal),
                    success: function (data) {
                        // Add Select Options
                        secondarySelect.empty();
                        $.each(data, function (i, selectOption) {
                            secondarySelect.append($('<option>', selectOption));
                        });
                    }
                });
            } else {
                // Empty select options as no primary option is selected
                secondarySelect.empty();
            }
        });
    }
};