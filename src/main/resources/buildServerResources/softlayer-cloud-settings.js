
if (!BS) BS = {};
if (!BS.IBMSoftlayer) BS.IBMSoftlayer = {};

if(!BS.IBMSoftlayer.ProfileSettingsForm) BS.IBMSoftlayer.ProfileSettingsForm = OO.extend(BS.PluginPropertiesForm, {

    checkConnectionUrl: '',
    propertiesBeanVsiTemplate: '',
    propertiesBeanDatacenter: '',
    
    _dataKeys: [ 'IBMSL_vsiTemplate', 'IBMSL_datacenter', 'IBMSL_agentName', 'IBMSL_MaxMemory', 'IBMSL_MaxCores', 'IBMSL_DiskType', 'IBMSL_network'],
    
    templates: {
        imagesTableRow: $j('<tr class="imagesTableRow">\
        		<td class="IBMSL_vsiTemplate highlight"></td>\
        		<td class="IBMSL_datacenter highlight"></td>\
        		<td class="IBMSL_agentName highlight"></td>\
        		<td class="IBMSL_MaxMemory highlight"></td>\
        		<td class="IBMSL_MaxCores highlight"></td>\
        		<td class="IBMSL_DiskType highlight"></td>\
			<td class="IBMSL_network highlight"></td>\
			<td class="edit highlight"><a href="#" class="editVmImageLink">edit</a></td>\
			<td class="remove"><a href="#" class="removeVmImageLink">delete</a></td>\
			        </tr>')},
        
        selectors: {
            rmImageLink: '.removeVmImageLink',
            editImageLink: '.editVmImageLink',
            imagesTableRow: '.imagesTableRow'
        },

        defaults: {
        		IBMSL_vsiTemplate: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_datacenter: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_agentName: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_MaxMemory: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_MaxCores: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_DiskType: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_network: '!SHOULD_NOT_BE_EMPTY!'
        },
        

        _errors: {
            badParam: 'Bad parameter',
            required: 'This field cannot be blank',
            requiredForFargate: 'This field is required when using FARGATE launch type',
            notSelected: 'Something should be selected',
            nonNegative: 'Must be non-negative number',
            nonPercentile: 'Must be a number from range 1..100'
        },
        
    _displayedErrors: {},
        
    initialize: function(){
         this.$ibmslUsername = $j(BS.Util.escapeId('IBMSL_username'));
         this.$ibmslApiKey = $j(BS.Util.escapeId('secure:IBMSL_apiKey'));
         this.$ibmslInstanceNumber = $j('#IBMSL_instanceNumber');
         
         
         
         this.$imagesTable = $j('#ecsImagesTable');
         this.$imagesTableWrapper = $j('.imagesTableWrapper');
         this.$emptyImagesListMessage = $j('.emptyImagesListMessage'); //TODO: implement
         this.$showAddImageDialogButton = $j('#showAddImageDialogButton');

         //add / edit image dialog
         this.$addImageButton = $j('#ecsAddImageButton');
         this.$cancelAddImageButton = $j('#ecsCancelAddImageButton');

         this.$deleteImageButton = $j('#ecsDeleteImageButton');
         this.$cancelDeleteImageButton = $j('#ecsCancelDeleteImageButton');

         this.$IBMSL_vsiTemplate = $j('#IBMSL_vsiTemplate');
         this.$IBMSL_datacenter = $j('#IBMSL_datacenter');
         this.$IBMSL_agentName = $j('#IBMSL_agentName');
         this.$IBMSL_MaxMemory = $j('#IBMSL_MaxMemory');
         this.$IBMSL_MaxCores = $j('#IBMSL_MaxCores');
         this.$IBMSL_DiskType = $j('#IBMSL_DiskType');
         this.$IBMSL_network = $j('#IBMSL_network');
         
         this.$imagesDataElem = $j('#' + 'source_images_json');

         var self = this;
         var rawImagesData = this.$imagesDataElem.val() || '[]';
         this._imagesDataLength = 0;
         try {
             var imagesData = JSON.parse(rawImagesData);
             this.imagesData = imagesData.reduce(function (accumulator, imageDataStr) {
                 accumulator[self._imagesDataLength++] = imageDataStr;
                 return accumulator;
             }, {});
         } catch (e) {
             this.imagesData = {};
             BS.Log.error('bad images data: ' + rawImagesData);
         }

         this._bindHandlers();
         this._renderImagesTable();

         BS.Clouds.Admin.CreateProfileForm.checkIfModified();
         
    },
    
    _bindHandlers: function () {
    	
        var self = this;

        this.$showAddImageDialogButton.on('click', this._showDialogClickHandler.bind(this));
        this.$addImageButton.on('click', this._submitDialogClickHandler.bind(this));
        this.$cancelAddImageButton.on('click', this._cancelDialogClickHandler.bind(this));

        this.$imagesTable.on('click', this.selectors.rmImageLink, function () {
            self.showDeleteImageDialog($j(this));
            return false;
        });
        this.$deleteImageButton.on('click', this._submitDeleteImageDialogClickHandler.bind(this));
        this.$cancelDeleteImageButton.on('click', this._cancelDeleteImageDialogClickHandler.bind(this));

        var editDelegates = this.selectors.imagesTableRow + ' .highlight, ' + this.selectors.editImageLink;
        var that = this;
        this.$imagesTable.on('click', editDelegates, function () {
            if (!that.$addImageButton.prop('disabled')) {
                self.showEditImageDialog($j(this));
            }
            return false;
        });
        
        
        
        this.$IBMSL_vsiTemplate.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_vsiTemplate.val(value);
            this._image['IBMSL_vsiTemplate'] = this.$IBMSL_vsiTemplate.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_datacenter.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_datacenter.val(value);
            this._image['IBMSL_datacenter'] = this.$IBMSL_datacenter.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_agentName.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_agentName.val(value);
            this._image['IBMSL_agentName'] = this.$IBMSL_agentName.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_MaxMemory.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_MaxMemory.val(value);
            this._image['IBMSL_MaxMemory'] = this.$IBMSL_MaxMemory.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_MaxCores.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_MaxCores.val(value);
            this._image['IBMSL_MaxCores'] = this.$IBMSL_MaxCores.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_DiskType.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_DiskType.val(value);
            this._image['IBMSL_DiskType'] = this.$IBMSL_DiskType.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_network.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_network.val(value);
            this._image['IBMSL_network'] = this.$IBMSL_network.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
    },
    
    _renderImagesTable: function () {
        this._clearImagesTable();

        if (this._imagesDataLength) {
            Object.keys(this.imagesData).forEach(function (imageId) {
                var image = this.imagesData[imageId];
                var src = image['source-id'];
                $j('#initial_images_list').val($j('#initial_images_list').val() + src + ",");
                this._renderImageRow(image, imageId);
            }.bind(this));
        }

        this._toggleImagesTable();
        BS.Clouds.Admin.CreateProfileForm.checkIfModified();
    },
    
    _clearImagesTable: function () {
        this.$imagesTable.find('.imagesTableRow').remove();
    },


    _toggleImagesTable: function () {
        var toggle = !!this._imagesDataLength;
        this.$imagesTableWrapper.removeClass('hidden');
        this.$emptyImagesListMessage.toggleClass('hidden', toggle);
        this.$imagesTable.toggleClass('hidden', !toggle);
    },
    
    _renderImageRow: function (props, id) {
        var $row = this.templates.imagesTableRow.clone().attr('data-image-id', id);
        var defaults = this.defaults;

        this._dataKeys.forEach(function (className) {
            $row.find('.' + className).text(props[className] || defaults[className]);
        });

        $row.find(this.selectors.rmImageLink).data('image-id', id);
        $row.find(this.selectors.editImageLink).data('image-id', id);
        this.$imagesTable.append($row);
    },
    
    _showDialogClickHandler: function () {
    	
        if (! this.$showAddImageDialogButton.attr('disabled')) {
        		this.checkConnection();
            this.showAddImageDialog();
        }
        return false;
    },

    _submitDialogClickHandler: function() {
        if (this.validateOptions()) {
            if (this.$addImageButton.val().toLowerCase() === 'save') {
                this.editImage(this.$addImageButton.data('image-id'));
            } else {
                this.addImage();
            }
            BS.IBMSoftlayer.ImageDialog.close();
        }
        return false;
    },

    _cancelDialogClickHandler: function () {
        BS.IBMSoftlayer.ImageDialog.close();
        return false;
    },
    
    showAddImageDialog: function () {
        $j('#EcsImageDialogTitle').text('Add IBM Softlayer Cloud Image');

        BS.Hider.addHideFunction('EcsImageDialog', this._resetDataAndDialog.bind(this));
        this.$addImageButton.val('Add').data('image-id', 'undefined');

        this._image = {};

        BS.IBMSoftlayer.ImageDialog.showCentered();
    },
    showEditImageDialog: function ($elem) {
    		
        var imageId = $elem.parents(this.selectors.imagesTableRow).data('image-id');

        $j('#EcsImageDialogTitle').text('Edit IBM Softlayer Cloud Image');

        BS.Hider.addHideFunction('EcsImageDialog', this._resetDataAndDialog.bind(this));

        typeof imageId !== 'undefined' && (this._image = $j.extend({}, this.imagesData[imageId]));
        this.$addImageButton.val('Save').data('image-id', imageId);
        if (imageId === 'undefined'){
            this.$addImageButton.removeData('image-id');
        }

        var image = this._image;
        
        this.$IBMSL_vsiTemplate.trigger('change', image['IBMSL_vsiTemplate'] || '');
        this.$IBMSL_datacenter.trigger('change', image['IBMSL_datacenter'] || '');
        this.$IBMSL_agentName.trigger('change', image['IBMSL_agentName'] || '');
        this.$IBMSL_MaxMemory.trigger('change', image['IBMSL_MaxMemory'] || '');
        this.$IBMSL_MaxCores.trigger('change', image['IBMSL_MaxCores'] || '');
        this.$IBMSL_DiskType.trigger('change', image['IBMSL_DiskType'] || '');
        this.$IBMSL_network.trigger('change', image['IBMSL_network'] || '');

        BS.IBMSoftlayer.ImageDialog.showCentered();
    },

    _resetDataAndDialog: function () {
        this._image = {};

        this.$IBMSL_vsiTemplate.trigger('change', '');
        this.$IBMSL_datacenter.trigger('change', '');
        this.$IBMSL_agentName.trigger('change', '');
        this.$IBMSL_MaxMemory.trigger('change', '');
        this.$IBMSL_MaxCores.trigger('change', '');
        this.$IBMSL_DiskType.trigger('change', '');
        this.$IBMSL_network.trigger('change', '');
    },

    validateOptions: function (options){
        var isValid = true;

        var validators = {

        		IBMSL_vsiTemplate : function () {
                    var IBMSL_vsiTemplate = this._image['IBMSL_vsiTemplate'];
                    if (!IBMSL_vsiTemplate || IBMSL_vsiTemplate === '' || IBMSL_vsiTemplate === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_vsiTemplate');
                        isValid = false;
                    }
                }.bind(this),
                
            IBMSL_datacenter : function () {
                    var IBMSL_datacenter = this._image['IBMSL_datacenter'];
                    if (!IBMSL_datacenter || IBMSL_datacenter === '' || IBMSL_datacenter === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_datacenter');
                        isValid = false;
                    }
                }.bind(this),
                
            IBMSL_agentName : function () {
                    var IBMSL_agentName = this._image['IBMSL_agentName'];
                    if (!IBMSL_agentName || IBMSL_agentName === '' || IBMSL_agentName === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_agentName');
                        isValid = false;
                    }
                }.bind(this),
                
            IBMSL_MaxMemory : function () {
                    var IBMSL_MaxMemory = this._image['IBMSL_MaxMemory'];
                    if (!IBMSL_MaxMemory || IBMSL_MaxMemory === '' || IBMSL_MaxMemory === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_MaxMemory');
                        isValid = false;
                    }
                }.bind(this),
                
           IBMSL_MaxCores : function () {
                    var IBMSL_MaxCores = this._image['IBMSL_MaxCores'];
                    if (!IBMSL_MaxCores || IBMSL_MaxCores === '' || IBMSL_MaxCores === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_MaxCores');
                        isValid = false;
                    }
                }.bind(this),
                
           IBMSL_DiskType : function () {
                    var IBMSL_DiskType = this._image['IBMSL_DiskType'];
                    if (!IBMSL_DiskType || IBMSL_DiskType === '' || IBMSL_DiskType === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_DiskType');
                        isValid = false;
                    }
                }.bind(this),
                                     
           IBMSL_network : function () {
        			  var IBMSL_network = this._image['IBMSL_network'];
                   if (!IBMSL_network || IBMSL_network === '' || IBMSL_network === undefined) {
	                    this.addOptionError('notSelected', 'IBMSL_network');
	                    isValid = false;
                   }
            	   }.bind(this),

        };

        if (options && ! $j.isArray(options)) {
            options = [options];
        }

        this.clearOptionsErrors(options);

        (options || this._dataKeys).forEach(function(option) {
            if(validators[option]) validators[option]();
        });

        return isValid;
    },

    addOptionError: function (errorKey, optionName) {
        var html;

        if (errorKey && optionName) {
            this._displayedErrors[optionName] = this._displayedErrors[optionName] || [];

            if (typeof errorKey !== 'string') {
                html = this._errors[errorKey.key];
                Object.keys(errorKey.props).forEach(function(key) {
                    html = html.replace('%%'+key+'%%', errorKey.props[key]);
                });
                errorKey = errorKey.key;
            } else {
                html = this._errors[errorKey];
            }

            if (this._displayedErrors[optionName].indexOf(errorKey) === -1) {
                this._displayedErrors[optionName].push(errorKey);
                this.addError(html, $j('.option-error_' + optionName));
            }
        }
    },

    addError: function (errorHTML, target) {
        target.append($j('<div>').html(errorHTML));
    },

    clearOptionsErrors: function (options) {
        (options || this._dataKeys).forEach(function (optionName) {
            this.clearErrors(optionName);
        }.bind(this));
    },

    clearErrors: function (errorId) {
        var target = $j('.option-error_' + errorId);
        if (errorId) {
            delete this._displayedErrors[errorId];
        }
        target.empty();
    },

    addImage: function () {
        var newImageId = this.generateNewImageId(),
            newImage = this._image;
        newImage['source-id'] = newImageId;
        this._renderImageRow(newImage, newImageId);
        this.imagesData[newImageId] = newImage;
        this._imagesDataLength += 1;
        this.saveImagesData();
        this._toggleImagesTable();
    },

    generateNewImageId: function () {
        if($j.isEmptyObject(this.imagesData)) return 0;
        else return Math.max.apply(Math, $j.map(this.imagesData, function callback(currentValue) {
            return currentValue['source-id'];
        })) + 1;
    },

    editImage: function (id) {
        this._image['source-id'] = id;
        this.imagesData[id] = this._image;
        this.saveImagesData();
        this.$imagesTable.find(this.selectors.imagesTableRow).remove();
        this._renderImagesTable();
    },

    removeImage: function (imageId) {
        delete this.imagesData[imageId];
        this._imagesDataLength -= 1;
        this.$imagesTable.find('tr[data-image-id=\'' + imageId + '\']').remove();
        this.saveImagesData();
        this._toggleImagesTable();
    },

    saveImagesData: function () {
        var imageData = Object.keys(this.imagesData).reduce(function (accumulator, id) {
            var _val = $j.extend({}, this.imagesData[id]);

            delete _val.$image;
            accumulator.push(_val);

            return accumulator;
        }.bind(this), []);
        this.$imagesDataElem.val(JSON.stringify(imageData));
    },

    showDeleteImageDialog: function ($elem) {
        var imageId = $elem.parents(this.selectors.imagesTableRow).data('image-id');

        BS.ajaxUpdater($("ecsDeleteImageDialogBody"), BS.IBMSoftlayer.DeleteImageDialog.url + window.location.search, {
            method: 'get',
            parameters : {
                imageId : imageId
            },
            onComplete: function() {
                BS.IBMSoftlayer.DeleteImageDialog.show(imageId);
            }
        });
    },
    
    _cancelDeleteImageDialogClickHandler: function () {
        BS.IBMSoftlayer.DeleteImageDialog.close();
        return false;
    },

    _submitDeleteImageDialogClickHandler: function() {
        var imageId = BS.IBMSoftlayer.DeleteImageDialog.currentImageId;
        BS.ajaxRequest(BS.IBMSoftlayer.DeleteImageDialog.url + window.location.search, {
            method: 'post',
            parameters : {
                imageId : imageId
            },
            onComplete: function() {
                BS.IBMSoftlayer.ProfileSettingsForm.removeImage(imageId);
                BS.IBMSoftlayer.DeleteImageDialog.close();
            }
        });
    },
    
    
    
    
    checkConnection: function() {
    	
	    var valid =	this.validateServerSettings();
	    var $fetchOptions = $j('#error_fetch_options');
	    var $loader = $j('.options-loader');
	    var $vsiTemplateOptions = $j('#IBMSL_vsiTemplate');
	    var $datacenterOptions = $j('#IBMSL_datacenter');
	    var propertiesBeanVsiTemplate = this.propertiesBeanVsiTemplate;
	    var propertiesBeanDatacenter = this.propertiesBeanDatacenter;
	    
	    if(valid)
	    	{
	    		$loader.removeClass('hidden');
	        BS.ajaxRequest(this.checkConnectionUrl, {
	            parameters: BS.Clouds.Admin.CreateProfileForm.serializeParameters(),
	            onFailure: function (response) {
	                BS.TestConnectionDialog.show(false, response, null);
	            }.bind(this),
	            onSuccess: function (response) {
	            	
	            	$loader.addClass('hidden');
	            var $response = $j(response.responseXML);
	            
	            //load error
	            $response.find('error').each(function(){
	             	$fetchOptions.text($j(this).text());
	            	});
	           
	            // load vsi template list in select option
	            $response.find('VsiPrivateTemplate').each(function(){
	            		$fetchOptions.text('');
	            		$vsiTemplateOptions.append('<option ' + (($j(this).attr('id') == propertiesBeanVsiTemplate)?'selected = "selected"':'') + ' value="'+$j(this).attr('id')+'">'+$j(this).attr('name')+'</option>');
	            });
	            
	            // load datacenter list in select option
	            $response.find('Datacenter').each(function(){
	            		$datacenterOptions.append('<option ' + (($j(this).attr('name') == propertiesBeanDatacenter)?'selected = "selected"':'') + ' value="'+$j(this).attr('name')+'">'+$j(this).attr('longName')+'</option>');
	            });
	           
	            }.bind(this)
	        });
	    }
    },
    
    validateServerSettings: function() {
	    	var isValid = true;
	    	[this.$ibmslUsername, this.$ibmslApiKey ].forEach(function($elem) {
	    		var val = $elem.val();
	    		var id = $elem.attr('id');
			var escapedId = BS.Util.escapeId('error_' + id);
	    		if (val != null && ! $elem.val().trim().length) {
	    			
	    			isValid = false;
	    			if (!isValid) {
	                    $j(escapedId).show();
	                    $j(escapedId).text('Value is required');
	            }
	    		}
	    		else {
	    			 $j(escapedId).text('');
	    		}
	    	});
	    	return isValid;
    }
    
});

if(!BS.IBMSoftlayer.ImageDialog) BS.IBMSoftlayer.ImageDialog = OO.extend(BS.AbstractModalDialog, {
    getContainer: function() {
        return $('EcsImageDialog');
    }
});

if(!BS.IBMSoftlayer.DeleteImageDialog) BS.IBMSoftlayer.DeleteImageDialog = OO.extend(BS.AbstractModalDialog, {
    url: '',
    currentImageId: '',

    getContainer: function() {
        return $('EcsDeleteImageDialog');
    },

    show: function (imageId) {
        BS.IBMSoftlayer.DeleteImageDialog.currentImageId = imageId;
        BS.IBMSoftlayer.DeleteImageDialog.showCentered();
    }
});