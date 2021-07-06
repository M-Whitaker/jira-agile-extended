(function($) {
  AJS.toInit(function() {
    $("#create-new-hierarchy-field").on('click', function (e) {
      e.preventDefault();
      console.debug("Adding new field...");
    });
    function multiSelectRender(self) {
      let array = [];
      array.push(`<select class="multi-select" size="4" multiple="multiple" name="${self.name}" id="${self.name}-multiselect">`);
      array.push(self.value.join("\n"));
      array.push(`</select>`);
      return array.join("\n");
    }
    new AJS.RestfulTable({
      el: $('#hierarchy-field-config-table'),
      autoFocus: true,
      allowCreate: false,
      model: AJS.RestfulTable.EntryModel.extend({
        date: function () {
          this.date();
        }
      }),
      resources: {
        all: AJS.contextPath()
            + '/rest/jira-agile-extended-admin/1.0/hierarchyfield/all?html=true',
        self: AJS.contextPath()
            + '/rest/jira-agile-extended-admin/1.0/hierarchyfield',
      },
      deleteConfirmationCallback: function(model) {
        // $(".aui-dialog2-content")[0].innerHTML = "<b>ID:</b> " + model.customfield_id + " <b>status:</b> " + model.issuetypes + " <b>description:</b> " + model.projects;
        AJS.dialog2("#demo-warning-dialog").show();
        return new Promise(function(resolve, reject) {
          $("#warning-dialog-confirm").on('click', function (e) {
            resolve();
            e.preventDefault();
            AJS.dialog2("#demo-warning-dialog").hide();
          });
          $(".aui-close-button, #warning-dialog-cancel").on('click', function (e) {
            reject();
            e.preventDefault();
            AJS.dialog2("#demo-warning-dialog").hide();
          });
        });
      },
      columns: [
        {
          id: 'customField',
          header: 'Custom Field',
          allowEdit: false,
          // readView: AJS.RestfulTable.CustomReadView.extend({
          //   render: function (self) {
          //     return `${self.value.name} (${self.value.id})`;
          //   }
          // }),
          // editView: AJS.RestfulTable.CustomEditView.extend({
          //   render: function (self) {
          //     return `${self.value.name} (${self.value.id})`;
          //   }
          // })
        },
        {
          id: 'issueTypes',
          header: 'Issue Types',
          readView: AJS.RestfulTable.CustomReadView.extend({
            render: function (self) {
              return multiSelectRender(self);
            }
          }),
          editView: AJS.RestfulTable.CustomEditView.extend({
            render: function(self) {
              return multiSelectRender(self);
            }
          })
        },
        {
          id: 'projects',
          header: 'JIRA Projects',
          readView: AJS.RestfulTable.CustomReadView.extend({
            render: function (self) {
              return multiSelectRender(self);
            }
          }),
          editView: AJS.RestfulTable.CustomEditView.extend({
            render: function (self) {
              return multiSelectRender(self);
            }
          })
        },
        {
          id: 'linkName',
          header: 'Link Name',
          allowEdit: false
        },
        {
          id: 'inwardLink',
          header: 'Inward Link',
          allowEdit: false
        },
        {
          id: 'outwardLink',
          header: 'Outward Link',
          allowEdit: false
        },
        {
          id: 'jqlStatement',
          header: 'JQL Statement',
          editView: AJS.RestfulTable.CustomEditView.extend({
            render: function (self) {
              return `<textarea class="textarea" name="${self.name}"
                      id="${self.id}" placeholder="Your jql statement here...">${self.value}</textarea>`;
            }
          })
        },
      ],
    });
  });
  // (function() {
  //   var restfulTableEvents = ["ROW_ADDED", "ROW_REMOVED", "EDIT_ROW", "REORDER_SUCCESS", "SERVER_ERROR"];
  //   restfulTableEvents.forEach(function(eventName) {
  //     jQuery(document).on(AJS.RestfulTable.Events[eventName], function() {
  //       console && console.log("RestfulTable event", eventName, "- callback arguments: ", arguments);
  //       AJS.flag({
  //         body: "<strong>" + eventName + "</strong> fired on RestfulTable. (Check devtools for more info).",
  //         close: "auto"
  //       });
  //     });
  //   });
  // })();
})(AJS.$ || jQuery);
