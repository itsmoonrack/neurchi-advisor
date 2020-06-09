"use strict";

const NAAddGroupWizard = function () {

    const options = {
        data: {
            type: 'remote',
            source: {
                read: {
                    url: '/me/groups',
                    timeout: 30000
                },
            },
            pageSize: 10,
            serverPaging: false,
            serverFiltering: false,
            serverSorting: false,
        },

        layout: {
            theme: 'default',
            class: '',
            footer: false,
            spinner: {
                message: "Chargement de vos groupes depuis Facebook..."
            }
        },

        sortable: true,

        filterable: false,

        pagination: true,

        columns: [{
            field: 'id',
            title: '#',
            sortable: false,
            width: 40,
            autoHide: false,
            selector: {
                class: 'kt-checkbox--solid'
            },
            textAlign: 'center'
        }, {
            field: 'name',
            title: 'Nom du groupe',
            width: 600,
            autoHide: false,
            template: function(data) {
                return '\
                    <div class="kt-user-card-v2">\
                        <div class="kt-user-card-v2__pic">\
                            <img src="' + data.picture + '" alt="picture" style="width: 40px; height: 40px; object-fit: cover">\
                        </div>\
                        <div class="kt-user-card-v2__details">\
                            <span class="kt-user-card-v2__name">' + data.name + '</span>\
                            <span class="kt-user-card-v2__email">' + data.description + '</span>\
                        </div>\
                    </div>';
            }
        }, {
            field: 'status',
            title: 'Statut',
            width: 100,
            autoHide: false,
            template: function(data) {
                const status = {
                    'Subscribed': {
                        'title': 'Souscrit',
                        'class': ' btn-label-success'
                    },
                    'Pending': {
                        'title': 'En attente',
                        'class': ' btn-label-brand'
                    },
                    'Manual': {
                        'title': 'Manuel',
                        'class': ' btn-label-brand'
                    },
                    'Blocked': {
                        'title': 'Bloqué',
                        'class': ' btn-label-warning'
                    },
                    '': {
                        'title': 'Non souscrit',
                        'class': ' btn-label-danger'
                    },
                };
                return '<span class="btn btn-bold btn-sm btn-font-sm ' + status[data.status].class + '">' + status[data.status].title + '</span>';
            }
            // }, {
            //     field: 'actions',
            //     title: 'Actions',
            //     width: 80,
            //     sortable: false,
            //     autoHide: false,
            //     overflow: 'visible',
            //     template: function() {
            //
            //         return '\
            //             <div class="dropdown">\
            //                 <a href="javascript:;" class="btn btn-sm btn-clean btn-icon btn-icon-md" data-toggle="dropdown">\
            //                     <i class="flaticon-more-1"></i>\
            //                 </a>\
            //                 <div class="dropdown-menu dropdown-menu-right">\
            //                     <ul class="kt-nav">\
            //                         <li class="kt-nav__item">\
            //                             <a href="#" class="kt-nav__link">\
            //                                 <i class="kt-nav__link-icon flaticon2-expand"></i>\
            //                                 <span class="kt-nav__link-text">Visualiser</span>\
            //                             </a>\
            //                         </li>\
            //                         <li class="kt-nav__item">\
            //                             <a href="#" class="kt-nav__link">\
            //                                 <i class="kt-nav__link-icon flaticon2-contract"></i>\
            //                                 <span class="kt-nav__link-text">Editer</span>\
            //                             </a>\
            //                         </li>\
            //                         <li class="kt-nav__item">\
            //                             <a href="#" class="kt-nav__link">\
            //                                 <i class="kt-nav__link-icon flaticon2-trash"></i>\
            //                                 <span class="kt-nav__link-text">Supprimer</span>\
            //                             </a>\
            //                         </li>\
            //                     </ul>\
            //                 </div>\
            //             </div>\
            //             ';
            //     }
        }]
    };

    const addGroupSelector = function () {

        // enable extension
        options.extensions = {
            checkbox: {},
        };

        const datatable = $('#group-record-selection').KTDatatable(options);

        datatable.on(
            'kt-datatable--on-click-checkbox kt-datatable--on-layout-updated',
            function(e) {
                // datatable.checkbox() access to extension methods
                var ids = datatable.checkbox().getSelectedId();
                var count = ids.length;
                $('#group-record-selected-number').html(count);
                if (count > 0) {
                    $('#group-record-action-form').collapse('show');
                } else {
                    $('#group-record-action-form').collapse('hide');
                }
            });

        $('#group-add-records').on('show.bs.modal', function(e) {
            const ids = datatable.rows('.kt-datatable__row--active')
                .nodes()
                .find('.kt-checkbox--single > [type="checkbox"]')
                .map(function (i, chk) {
                    return $(chk).val();
                });
            const names = datatable.rows('.kt-datatable__row--active')
                .nodes()
                .find('.kt-user-card-v2__name')
                .map(function (i, element) {
                    return $(element).text();
                });
            const c = document.createDocumentFragment();
            for (let i = 0; i < names.length; i++) {
                const li = document.createElement('li');
                li.innerHTML = names[i];
                c.appendChild(li);
            }
            const groupIds = [];
            for (let i = 0; i < ids.length; i++) {
                groupIds[i] = ids[i];
            }
            $(e.target).find('.btn-primary').click(function () {
                KTApp.block('#group-add-records .modal-content', {});
                $.ajax('/me/subscriptions', {
                    type: 'POST',
                    data: JSON.stringify(groupIds),
                    dataType: 'json',
                    contentType: 'application/json',
                    success: function (data, status, xhr) {
                        window.location.replace('apps/group/list-default.html')
                    },
                    error: function (xhr, status, message) {
                        console.log('nok');
                        KTApp.unblock('#group-add-records .modal-content');
                    }
                });
            });
            $(e.target).find('.group-add-records-modal-selected-ids').append(c);
        }).on('hide.bs.modal', function(e) {
            $(e.target).find('.group-add-records-modal-selected-ids').empty();
        });
    };

    return {
        init: function () {
            addGroupSelector();
        }
    };
}();

const NAAjaxSetup = function () {

    const csrf = {
        token: $('meta[name=csrf_token]').attr('content'),
        headerName: $('meta[name=csrf_header_name]').attr('content')
    };

    const initCsrfToken = function () {

        $.ajaxSetup({
            headers: {
                'X-CSRF-Token': csrf.token,
            },
        });

    };

    return {
        init: function () {
            initCsrfToken();
        }
    };
}();

jQuery(document).ready(function() {
    NAAjaxSetup.init();
    NAAddGroupWizard.init();
});