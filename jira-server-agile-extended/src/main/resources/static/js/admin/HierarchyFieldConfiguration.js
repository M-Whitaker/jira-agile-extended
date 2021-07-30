(function($) {
  AJS.toInit(function() {
    function multiSelectRender(self) {
      let array = [];
      array.push(
          `<select class="multi-select" size="5" multiple="multiple" name="${self.name}" id="${self.name}-multiselect">`);
      self.value.forEach(value => {
        array.push(
            `<option value=${JSON.stringify(value)}${value.selected
                ? ' selected>'
                : '>'}${value.name}</option>`);
      });
      array.push('</select>');
      return array.join('\n');
    }
    new AJS.RestfulTable({
      el: $('#hierarchy-field-config-table'),
      autoFocus: true,
      allowCreate: false,
      resources: {
        all: AJS.contextPath()
            + '/rest/jira-agile-extended-admin/1.0/hierarchyfield/all',
        self: AJS.contextPath()
            + '/rest/jira-agile-extended-admin/1.0/hierarchyfield',
      },
      deleteConfirmationCallback: function(model) {
        // $(".aui-dialog2-content")[0].innerHTML = "<b>ID:</b> " + model.customfield_id + " <b>status:</b> " + model.issuetypes + " <b>description:</b> " + model.projects;
        AJS.dialog2('#demo-warning-dialog').show();
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
          readView: AJS.RestfulTable.CustomReadView.extend({
            render: function(self) {
              return `${self.value.name} (${self.value.id})`;
            },
          }),
          editView: AJS.RestfulTable.CustomReadView.extend({
            render: function(self) {
              return `${self.value.name} (${self.value.id})`;
            },
          }),
          // editView: AJS.RestfulTable.CustomEditView.extend({
          //   render: function (self) {
          //     return `<input class="text" type="text" name="${self.name}"
          //             id="${self.id}" value="${self.value.name}">`;
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
          id: 'issueLink',
          header: 'Issue Link',
          allowEdit: false,
          readView: AJS.RestfulTable.CustomReadView.extend({
            render: function(self) {
              return `${self.value.name} (${self.value.id})`;
            },
          }),
          editView: AJS.RestfulTable.CustomReadView.extend({
            render: function(self) {
              return `${self.value.name} (${self.value.id})`;
            },
          }),
        },
        {
          id: 'inwardLink',
          header: 'Inward Link'
        },
        {
          id: 'outwardLink',
          header: 'Outward Link'
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
})(AJS.$ || jQuery);
