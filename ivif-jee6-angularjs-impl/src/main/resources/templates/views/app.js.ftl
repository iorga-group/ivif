
'use strict';

angular.module('${model.configuration.angularModuleName}', [
        'ngRoute',
        'ngTable',
        'angularMoment',
        'mgcrea.ngStrap'<#list model.configuration.angularModuleImports as import>,
        '${import}'</#list>
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
    .factory('locationService', ['$location', '$filter', '$rootScope', function($location, $filter, $rootScope) {
        var newLocationToBePushed = false,
            locationContexts = [],
            locationLoading = false,
            currentDirtyCheckKey = 0,
            locationService = {
                controllerInitialized: function(title, scope, scopeAttributesToSave) {
                    var locationContext = {
                        title: title,
                        originalTitle: title,
                        scope: scope,
                        scopeAttributesToSave: scopeAttributesToSave,
                        path: $location.path(),
                        search: $location.search(),
                        savedScopeAttributes: null,
                        dirtyChecks: {}
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
                loadLocationContextWithIndex: function(locationIndex) {
                    // Deleting previous location Context
                    if (locationService.checkDirtyAndRemoveLocationContext(locationIndex)) {
                        var locationContext = locationService.getCurrentLocationContext();
                        locationService.loadLocationContext(locationContext);
                    }
                },
                checkDirtyAndRemoveLocationContext: function(locationIndex, onlyReturnConfirmMessage) {
                    var lastLocation = true;
                    for (var i = locationContexts.length - 1 ; i > locationIndex ; i--) {
                        var locationContext = locationContexts[i];
                        var dirtyChecks = locationContext.dirtyChecks;
                        // Check for dirty checks to confirm to leave before really leaving that locationContext
                        for (var dirtyCheckKey in dirtyChecks) {
                            if (dirtyChecks[dirtyCheckKey]()) {
                                // this is dirty, we must confirm this to leave
                                var title = lastLocation ? $filter('interpolate')(locationContext.originalTitle, locationContext.scope) : locationContext.title;
                                var message = 'There are some unsaved changes in tab "' + title + '", do you really want to leave and loose those changes?';
                                if (!onlyReturnConfirmMessage) {
                                    if (!confirm(message)) {
                                        if (lastLocation) {
                                            return false; // the user didn't want to loose the changes, but we are already at this location
                                        } else {
                                            // the user didn't want to loose the changes, let's go to this location
                                            locationService.loadLocationContext(locationContext);
                                            return false; // Don't execute the rest of the code, because we loaded another location context than asked at first
                                        }
                                    }
                                } else {
                                    locationService.loadLocationContext(locationContext);
                                    return message;
                                }
                            }
                        }
                        locationContexts.pop(); // all dirty checks passed, we can safely remove this locationContext
                        lastLocation = false;
                    }
                    return true;
                },
                checkAllDirtyLocationContextsBeforeLeaving: function(onlyReturnConfirmMessage) {
                    return locationService.checkDirtyAndRemoveLocationContext(-1, onlyReturnConfirmMessage);
                },
                loadLocationContext: function(locationContext) {
                    locationLoading = true;
                    // Recover original title
                    locationContext.title = locationContext.originalTitle;
                    // Go to asked location
                    $rootScope.$evalAsync(function() {
                        // in an $evalAsync because sometimes we are trying to go to a new $location AND want to cancel the current attempt. See https://github.com/angular/angular.js/issues/9607#issuecomment-59087310
                        $location.path(locationContext.path).search(locationContext.search);
                    });
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
                },
                addDirtyCheck: function(dirtyCheck) {
                    var dirtyCheckKey = String.valueOf(currentDirtyCheckKey++);
                    var locationContext = locationService.getCurrentLocationContext();
                    locationContext.dirtyChecks[dirtyCheckKey] = dirtyCheck;
                    return dirtyCheckKey;
                },
                removeDirtyCheck: function(dirtyCheckKey) {
                    // Search for the locationContext which contains that key
                    for (var i = locationContexts.length - 1 ; i > 0 ; i--) {
                        var dirtyCheck = locationContexts[i].dirtyChecks[dirtyCheckKey];
                        if (dirtyCheck) {
                            delete locationContexts[i].dirtyChecks[dirtyCheckKey];
                            break; // there can be only one dirtyCheck per key
                        }
                    }
                },
                isLocationLoading: function() {
                    return locationLoading;
                },
                isNewLocationToBePushed: function() {
                    return newLocationToBePushed;
                }
            };
        return locationService;
    }])
    .run(['locationService', '$window', '$rootScope', function(locationService, $window, $rootScope) {
        // register the dirty guards which will prevent the user to leave unsaved modifications
        $rootScope.$on('$locationChangeStart', function(event) {
            if (!locationService.isLocationLoading() && !locationService.isNewLocationToBePushed() && !locationService.checkAllDirtyLocationContextsBeforeLeaving()) {
                // the location change event has been intercepted by the user, let's cancel the event
                event.preventDefault();
            }
        });
        $window.onbeforeunload = function() {
            var message = locationService.checkAllDirtyLocationContextsBeforeLeaving(true);
            if (message !== true) {
                return message;
            }
        }
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
                    if (angular.isArray(messages)) {
                        // Multiple messages
                        angular.forEach(messages, function (message) {
                            messageService.addMessage(message);
                        });
                    } else if (angular.isString(messages)) {
                        // Simple string message
                        messageService.addMessage({title: 'Server message', level: 'ERROR', message: messages});
                    } else if (angular.isObject(messages)) {
                        // Simple message
                        messageService.addMessage(messages);
                    }
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
        <#-- Thanks to http://stackoverflow.com/a/19771501/535203 -->
        //initialize get if not there
        if (!$httpProvider.defaults.headers.get) {
            $httpProvider.defaults.headers.get = {};
        }

        //disable IE ajax request caching
        $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Thu, 01 Jan 1970 00:00:00 GMT';
        // extra
        $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
        $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
    }])
    .directive('gridLineField', function() {<#-- Thanks to http://stackoverflow.com/a/24470458/535203 -->
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, iElement, iAttrs, modelCtrl) {
                var fieldName = iAttrs.ngModel.substring('line.'.length),
                    modelCtrls = scope.line.$modelCtrls;
                if (!modelCtrls) {
                    modelCtrls = {};
                    scope.line.$modelCtrls = modelCtrls;
                }
                modelCtrls[fieldName] = modelCtrl; // attach current ngModelController to the line
                // attach on change listener
                modelCtrl.$viewChangeListeners.push(function() {
                    scope.onLineValueChange(scope.line, fieldName);
                });
                // attach on validity state change listener
                scope.$watch(function() {return modelCtrl.$valid;}, function(newValue, oldValue) {
                    if (newValue !== oldValue) {
                        scope.onLineValidStatusChange(scope.line, fieldName);
                    }
                });
            }
        };
    })
;