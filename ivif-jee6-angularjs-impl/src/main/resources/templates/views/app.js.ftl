'use strict';

angular.module('${model.configuration.angularModuleName}', [
        'ngRoute',
        'ngTable',
        'angularMoment',
        'ui.bootstrap'
    ])
    .value('locationUtils', {
        fromObjectToSearch: function(object) {
            var search = {};
            angular.forEach(object, function(value, key) {
               search[key] = angular.toJson(value);
            });
            return search;
        },
        fromSearchToObject: function (search) {
            var object = {};
            angular.forEach(search, function(value, key) {
                object[key] = angular.fromJson(value);
            });
            return object;
        }
    })
    .factory('locationService', ['$location', '$filter', function($location, $filter) {
        var newLocationToBePushed = false,
            locationContexts = [],
            locationLoading = false,
            locationService = {
                controllerInitialized: function(title, scope, scopeAttributesToSave) {
                    var locationContext = {
                        title: title,
                        originalTitle: title,
                        scope: scope,
                        scopeAttributesToSave: scopeAttributesToSave,
                        path: $location.path(),
                        search: $location.search(),
                        savedScopeAttributes: null
                    };
                    if (!locationLoading) {
                        if (!newLocationToBePushed) {
                            locationContexts = []; // re-init location contexts because it's a new root
                        }
                        locationContexts.push(locationContext);
                    }
                    newLocationToBePushed = false;
                    locationLoading = false;
                },
                pushNewLocation: function(path, search) {
                    newLocationToBePushed = true;
                    // Save current attributes values
                    var locationContext = locationService.getCurrentLocationContext(),
                        savedScopeAttributes = {},
                        scope = locationContext.scope;
                    locationContext.savedScopeAttributes = savedScopeAttributes;
                    angular.forEach(locationContext.scopeAttributesToSave, function(scopeAttributeToSave) {
                        savedScopeAttributes[scopeAttributeToSave] = scope[scopeAttributeToSave];
                    });
                    // Freeze the title
                    locationContext.title = $filter('interpolate')(locationContext.originalTitle, scope);
                    // Go to asked new location
                    $location.path(path).search(search);
                },
                loadLocationContext: function(locationIndex) {
                    // Deleting previous location Context
                    for (var i = locationContexts.length - 1 ; i > locationIndex ; i--) {
                        locationContexts.pop();
                    }
                    var locationContext = locationService.getCurrentLocationContext();
                    locationLoading = true;
                    // Recover original title
                    locationContext.title = locationContext.originalTitle;
                    // Go to asked location
                    $location.path(locationContext.path).search(locationContext.search);
                },
                initializeController: function(scope) {
                    if (locationLoading) {
                        // we are loading a previous saved location context
                        // get the current location context in order to apply saved context values
                        var locationContext = locationService.getCurrentLocationContext(),
                            savedScopeAttributes = locationContext.savedScopeAttributes;
                        angular.forEach(locationContext.scopeAttributesToSave, function(scopeAttributeToSave) {
                            scope[scopeAttributeToSave] = savedScopeAttributes[scopeAttributeToSave];
                        });
                        // Change current scope to the new controller one
                        locationContext.scope = scope;
                        return true; // controller initialized
                    } else {
                        return false; // controller must initialize
                    }
                },
                getCurrentLocationContext: function() {
                    return locationContexts.length > 0 ? locationContexts[locationContexts.length - 1] : null;
                },
                getLocationContexts: function() {
                    return locationContexts;
                }
            };
        return locationService;
    }])
    .filter('interpolate', ['$interpolate', function($interpolate) {
        return function(str, scope) {
            return $interpolate(str)(scope);
        }
    }])
    .factory('headerUtil', ['$window', function($window) {
        return {
            fromServerToObject: function(serverStr) {
                return angular.fromJson($window.atob(serverStr));
            }
        }
    }])
    .factory('messageService', [function() {
        var messageService = {
            addMessage: function(message) {
                if (message.type && message.type === 'MODAL') {
                    messageService.addModalMessage(message);
                } else {
                    messageService.addAlertMessage(message);
                }
            },
            addModalMessage: function(message) {
                var messageElement = '<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true"><div class="modal-dialog"><div class="modal-content panel-'+messageService.getLevelClassSuffix(message)+'"><div class="modal-header panel-heading">';
                if (message.title) {
                    messageElement += '<h4 class="modal-title">'+message.title+'</h4>';
                }
                messageElement += '</div><div class="modal-body">'+message.message+'</div>';
                messageElement += '<div class="modal-footer"><button type="button" class="btn btn-primary" data-dismiss="modal">OK</button></div></div></div></div>';
                var jElement = angular.element(messageElement);
                jElement.appendTo(messageService.getModalMessagesRootElement())
                    .modal({show: true})
                    .on('hidden.bs.modal', function() {
                        jElement.remove();
                    });
            },
            addAlertMessage: function(message) {
                var messageElement = '<div class="alert alert-'+messageService.getLevelClassSuffix(message)+' alert-dismissible fade in col-md-10 col-md-offset-1" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>';
                if (message.title) {
                    messageElement += '<h4>'+message.title+'</h4>';
                }
                messageElement += message.message + '</div>';
                angular.element(messageElement).appendTo(messageService.getAlertMessagesRootElement()).alert();
            },
            getLevelClassSuffix: function(message) {
                var klass = (message.level || 'info').toLowerCase();
                if (klass === 'error') {
                    klass = 'danger';
                }
                return klass;
            },
            getAlertMessagesRootElement: function() {
                var messagesRootElement = angular.element('.ivifAlertMessagesRoot');
                if (messagesRootElement.length == 0) {
                    // the root element does not exist, let's create it
                    angular.element(document.body).append('<div class="container-fluid"><div class="row"><div class="row col-md-12 ivifAlertMessagesRoot" style="position: fixed; top: 20%"></div></div></div>');
                    messagesRootElement = angular.element('.ivifAlertMessagesRoot');
                }
                return messagesRootElement;
            },
            getModalMessagesRootElement: function () {
                var messagesRootElement = angular.element('.ivifModalMessagesRoot');
                if (messagesRootElement.length == 0) {
                    // the root element does not exist, let's create it
                    angular.element(document.body).append('<div class="ivifModalMessagesRoot"></div>');
                    messagesRootElement = angular.element('.ivifModalMessagesRoot');
                }
                return messagesRootElement;
            }
        };
        return messageService;
    }])
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push(['$q', 'headerUtil', 'messageService', function($q, headerUtil, messageService) {
            function interceptExceptionOrMessages(config) {
                var exception = config.headers('X-IVIF-JA-Exception'),
                    messages = config.headers('X-IVIF-JA-Messages');
                if (exception) {
                    exception = headerUtil.fromServerToObject(exception);
                    messageService.addModalMessage({
                        title: 'Problem on the server side',
                        level: 'ERROR',
                        message: '<p>A problem appeared on the server side: '+exception.message+'</p>' +
                        '<p><button class="btn" type="button" data-toggle="collapse" data-target="#ivifExceptionModal" aria-expended="false" aria-controls="ivifExceptionModal">Details...</button>' +
                        '<div class="collapse" id="ivifExceptionModal"><div class="well"><p>'+exception.className+'#'+exception.uuid+'<pre>'+config.data+'</pre></p></div></div></p>'
                    });
                }
                if (messages) {
                    messages = headerUtil.fromServerToObject(messages);
                    angular.forEach(messages, function(message) {
                        messageService.addMessage(message);
                    });
                }
                return exception || messages;
            }
            return {
                'response': function(response) {
                    interceptExceptionOrMessages(response);
                    return response;
                },
                'responseError': function(rejection) {
                    if (!interceptExceptionOrMessages(rejection)) {
                        // No message was shown, must show a problem
                        messageService.addModalMessage({
                            title: 'Error '+rejection.status,
                            level: 'ERROR',
                            message: '<p>A problem appeared on the server side (status '+rejection.status+')</p>' +
                            '<p><button class="btn" type="button" data-toggle="collapse" data-target="#ivifResponseErrorModal" aria-expended="false" aria-controls="ivifResponseErrorModal">Details...</button>' +
                            '<div class="collapse" id="ivifResponseErrorModal"><div class="well">'+rejection.data+'</div></div></p>'
                        });
                    }
                    return $q.reject(rejection);
                }
            }
        }]);
    }])
;