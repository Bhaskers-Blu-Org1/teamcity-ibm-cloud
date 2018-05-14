if (!BS) BS = {};
if (!BS.IBMSoftlayer) BS.IBMSoftlayer = {};

if(!BS.IBMSoftlayer.ProfileSettingsForm) BS.IBMSoftlayer.ProfileSettingsForm = OO.extend(BS.PluginPropertiesForm, {

    checkConnectionUrl: '',
    propertiesBeanVsiTemplate: '',
    propertiesBeanDatacenter: '',
    
    _dataKeys: [ 'IBMSL_vsiTemplate', 'IBMSL_datacenter', 'IBMSL_agentName', 'IBMSL_domainName', 'IBMSL_maxMemory', 'IBMSL_maxCores', 'IBMSL_diskType', 'IBMSL_network', 'IBMSL_vsiBilling', 'agent_pool_id', 'IBMSL_maximumInstances'],
    
    templates: {
        imagesTableRow: $j('<tr class="imagesTableRow">\
        		<td class="IBMSL_vsiTemplate highlight"></td>\
        		<td class="IBMSL_datacenter highlight"></td>\
        		<td class="IBMSL_agentName highlight"></td>\
        		<td class="IBMSL_domainName highlight"></td>\
        		<td class="IBMSL_maxMemory highlight"></td>\
        		<td class="IBMSL_maxCores highlight"></td>\
        		<td class="IBMSL_diskType highlight"></td>\
			<td class="IBMSL_network highlight"></td>\
        		<td class="IBMSL_vsiBilling highlight"></td>\
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
        		IBMSL_domainName: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_maxMemory: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_maxCores: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_diskType: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_network: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_vsiBilling: '!SHOULD_NOT_BE_EMPTY!'
        },

        _errors: {
        	 	agentNameBadParam: 'Not a valid Agent Name',
        		domainNameBadParam: 'Not a valid Domain Name',
            required: 'This field cannot be blank',
            requiredForFargate: 'This field is required when using FARGATE launch type',
            notSelected: 'Something should be selected',
            nonNegative: 'Must be non-negative number',
            nonPercentile: 'Must be a number from range 1..100'
            maximumInstances: 'Must be a positive integer or leave blank'
        },
        
    _displayedErrors: {},
        
    initialize: function(){
         this.$IBMSL_username = $j(BS.Util.escapeId('IBMSL_username'));
         this.$IBMSL_apiKey = $j(BS.Util.escapeId('secure:IBMSL_apiKey'));
         this.$IBMSL_instanceNumber = $j('#IBMSL_instanceNumber');
         
         this.$imagesTable = $j('#softlayerImagesTable');
         this.$imagesTableWrapper = $j('.imagesTableWrapper');
         this.$emptyImagesListMessage = $j('.emptyImagesListMessage'); //TODO: implement
         this.$showAddImageDialogButton = $j('#showAddImageDialogButton');

         // add/edit image dialog
         this.$addImageButton = $j('#softlayerAddImageButton');
         this.$cancelAddImageButton = $j('#softlayerCancelAddImageButton');
         this.$deleteImageButton = $j('#softlayerDeleteImageButton');
         this.$cancelDeleteImageButton = $j('#softlayerCancelDeleteImageButton');

         this.$IBMSL_vsiTemplate = $j('#IBMSL_vsiTemplate');
         this.$IBMSL_datacenter = $j('#IBMSL_datacenter');
         this.$IBMSL_agentName = $j('#IBMSL_agentName');
         this.$IBMSL_domainName = $j('#IBMSL_domainName');
         this.$IBMSL_maxMemory = $j('#IBMSL_maxMemory');
         this.$IBMSL_maxCores = $j('#IBMSL_maxCores');
         this.$IBMSL_diskType = $j('#IBMSL_diskType');
         this.$IBMSL_network = $j('#IBMSL_network');
         this.$IBMSL_vsiBilling = $j('#IBMSL_vsiBilling');
         this.$agentPoolId = $j('#agent_pool_id');
         this.$IBMSL_maximumInstances = $j('#IBMSL_maximumInstances');
         
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
         this._toggleShowAddImageDialogButton(false);

         BS.Clouds.Admin.CreateProfileForm.checkIfModified();
         
    },
    
    _toggleShowAddImageDialogButton: function (enable) {
        
    		// Toggling disable attribute on ShowAddImageDialogButton
    		this.$showAddImageDialogButton.attr('disabled', !enable);
    },
    
    
    _bindHandlers: function () {
    		
    		// Binding events on DOM elements
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
        
        // On chnage of username and apikey call checkConnection()
        this.$IBMSL_username.on('change', function (e, value) {
        		this.checkConnection();
        }.bind(this));
        
        this.$IBMSL_apiKey.on('change', function (e, value) {
        		this.checkConnection();
        }.bind(this));
        
        
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
        
        this.$IBMSL_domainName.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_domainName.val(value);
            this._image['IBMSL_domainName'] = this.$IBMSL_domainName.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_maxMemory.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_maxMemory.val(value);
            this._image['IBMSL_maxMemory'] = this.$IBMSL_maxMemory.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_maxCores.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_maxCores.val(value);
            this._image['IBMSL_maxCores'] = this.$IBMSL_maxCores.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_diskType.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_diskType.val(value);
            this._image['IBMSL_diskType'] = this.$IBMSL_diskType.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_network.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_network.val(value);
            this._image['IBMSL_network'] = this.$IBMSL_network.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_vsiBilling.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_vsiBilling.val(value);
            this._image['IBMSL_vsiBilling'] = this.$IBMSL_vsiBilling.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        
        this.$agentPoolId.on('change', function (e, value) {
        	    if(value !== undefined) this.$agentPoolId.val(value);
        	    this._image['agent_pool_id'] = this.$agentPoolId.val();
        	    this.validateOptions(e.target.getAttribute('data-id'));
        	}.bind(this));
        
        this.$IBMSL_maximumInstances.on('change', function (e, value) {
        	if(value !== undefined) this.$IBMSL_maximumInstances.val(value);
        	this._image['IBMSL_maximumInstances'] = this.$IBMSL_maximumInstances.val();
        	this.validateOptions(e.target.getAttribute('data-id'));
        }
    },
    
    _renderImagesTable: function () {
    	
    		// Method passes image and imageId to _renderImageRow for creating and appending cloudimage row to table
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
    		
    		//This function creates each row of cloud image table and appends.
    	
        var $row = this.templates.imagesTableRow.clone().attr('data-image-id', id);
        var defaults = this.defaults;

        this._dataKeys.forEach(function (className) {
        		if(className == 'IBMSL_vsiTemplate')
        			{
        				var vsiTemplateValueJson = JSON.parse(props[className]);
        				$row.find('.' + className).text(vsiTemplateValueJson.name.replace(/&nbsp;/g,' ') || defaults[className]);
        			}
        		else if(className == 'IBMSL_diskType' || className == 'IBMSL_vsiBilling')
        			{
        				var json = JSON.parse(props[className]);
        				$row.find('.' + className).text(json.type || defaults[className]);
        			}
        		else
        			{
        			$row.find('.' + className).text(props[className] || defaults[className]);
        			}
        });

        $row.find(this.selectors.rmImageLink).data('image-id', id);
        $row.find(this.selectors.editImageLink).data('image-id', id);
        this.$imagesTable.append($row);
    },
    
    _showDialogClickHandler: function () {
    		
    		// showAddImageDialogButton click handler
        if (! this.$showAddImageDialogButton.attr('disabled')) {
        		this.checkConnection();
            this.showAddImageDialog();
        }
        return false;
    },

    _submitDialogClickHandler: function() {
    	
    		// Saves detail of cloud image and update table row
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
        $j('#softlayerImageDialogTitle').text('Add IBM Softlayer Cloud Image');
        BS.Hider.addHideFunction('softlayerImageDialog', this._resetDataAndDialog.bind(this));
        this.$addImageButton.val('Add').data('image-id', 'undefined');
        this._image = {};
        BS.IBMSoftlayer.ImageDialog.showCentered();
    },
    
    showEditImageDialog: function ($elem) {
    		
    		//opens the edit image dialog box
        var imageId = $elem.parents(this.selectors.imagesTableRow).data('image-id');

        $j('#softlayerImageDialogTitle').text('Edit IBM Softlayer Cloud Image');

        BS.Hider.addHideFunction('softlayerImageDialog', this._resetDataAndDialog.bind(this));

        typeof imageId !== 'undefined' && (this._image = $j.extend({}, this.imagesData[imageId]));
        this.$addImageButton.val('Save').data('image-id', imageId);
        if (imageId === 'undefined'){
            this.$addImageButton.removeData('image-id');
        }

        var image = this._image;
        
        this.$IBMSL_vsiTemplate.trigger('change', image['IBMSL_vsiTemplate'] || '');
        this.$IBMSL_datacenter.trigger('change', image['IBMSL_datacenter'] || '');
        this.$IBMSL_agentName.trigger('change', image['IBMSL_agentName'] || '');
        this.$IBMSL_domainName.trigger('change', image['IBMSL_domainName'] || '');
        this.$IBMSL_maxMemory.trigger('change', image['IBMSL_maxMemory'] || '');
        this.$IBMSL_maxCores.trigger('change', image['IBMSL_maxCores'] || '');
        this.$IBMSL_diskType.trigger('change', image['IBMSL_diskType'] || '');
        this.$IBMSL_network.trigger('change', image['IBMSL_network'] || '');
        this.$IBMSL_vsiBilling.trigger('change', image['IBMSL_vsiBilling'] || '');
        this.$agentPoolId.trigger('change', image['agent_pool_id'] || '');
        this.$IBMSL_maximumInstances.trigger('change', image['IBMSL_maximumInstances'] || '');

        BS.IBMSoftlayer.ImageDialog.showCentered();
    },

    _resetDataAndDialog: function () {
        this._image = {};

        this.$IBMSL_vsiTemplate.trigger('change', '');
        this.$IBMSL_datacenter.trigger('change', '');
        this.$IBMSL_agentName.trigger('change', '');
        this.$IBMSL_domainName.trigger('change', '');
        this.$IBMSL_maxMemory.trigger('change', '');
        this.$IBMSL_maxCores.trigger('change', '');
        this.$IBMSL_diskType.trigger('change', '');
        this.$IBMSL_network.trigger('change', '');
        this.$IBMSL_vsiBilling.trigger('change', '');
        this.$agentPoolId.trigger('change', '');
        this.$IBMSL_maximumInstances.trigger('change', '');
    },

    validateOptions: function (options){
    		
    		//validates cloud image parameters
    	
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
                    
                    /* agentRegExp1 checks: 
                    	* 1) Must begin/end with an alphanumeric character. 
                    	* 2) Can contain non-consecutive '-' dash.
                    	* */
                    	var agentRegExp1 = new RegExp(/^[A-Za-z0-9]+(-[A-Za-z0-9]+)*$/,'g');
                                        
                    	/* agentRegExp2 checks:
                    	* 1) String does not consist of only digits. Must contain at least one alphabetic character.
                    	* */
                    	var agentRegExp2 = new RegExp(/^[0-9]+(-[0-9]+)*$/,'g'); 
                                       
                    	/* agentRegExp3 checks:
                    	* 1) String length is min:1 to max:63
                    	* */
                    	var agentRegExp3 = new RegExp(/^.{1,63}$/,'g'); 
                    	if (!IBMSL_agentName || IBMSL_agentName === '' || IBMSL_agentName === undefined || !agentRegExp1.test(IBMSL_agentName) || agentRegExp2.test(IBMSL_agentName) || !agentRegExp3.test(IBMSL_agentName)) {
                        this.addOptionError('agentNameBadParam', 'IBMSL_agentName');
                        isValid = false;
                    }
            }.bind(this),
                
            IBMSL_domainName : function () {
                    var IBMSL_domainName = this._image['IBMSL_domainName'];
                    /* domainRegExp1 checks:
                    	* 1) Each alphanumeric string separated by a period is considered a label. The last label, the TLD (top level domain).
                    	* 2) The domain portion must consist of least one label followed by a period '.' then ending with the TLD label.
                    	* 3) Labels must begin and end with an alphanumeric character.
                    	* 4) Label can use '-'. And '-' & '.' shall not be adjacent.
                    	* */
                    	var domainRegExp1 = new RegExp(/^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)*(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)$/,'g');
                                       
                    	/* domainRegExp2 checks:
                    	* 1) TLd is not solely comprised of only digits.
                    	* Test: True -> TLD only contains digits.
                    	* */
                    	var domainRegExp2 = new RegExp(/^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)*(\.[0-9]+(-[0-9]+)*)$/,'g');
                                      
                   	/* domainRegExp2 checks:
                    	* 1) Length of each Label is min:1 to max:63
                    	* 2) The last label, the TLD (top level domain) must be between 2-24 alphabetic characters.
                    	* */
                     var domainRegExp3 = new RegExp(/^([A-Za-z0-9-]{1,63})((\.[A-Za-z0-9-]{1,63}){0,})(\.[A-Za-z0-9-]{2,23})$/,'g'); 
                                       
                    	/* domainRegExp3 checks:
                    	* 1) Combining the agentname, followed by a period '.', followed by the domain gives the FQDN (fully qualified domain name), 
                    	* which may not exceed 253 characters in total length.
                    	* 2) String (including '.') length is min:2 to max:189 (253-63-1 = 189 [253-agentnNameLength-periodFollowedByAgentname])
                    	* */
                    	var domainRegExp4 = new RegExp(/^.{2,189}$/,'g'); 
                    	
                    	if (!IBMSL_domainName || IBMSL_domainName === '' || IBMSL_domainName === undefined || !domainRegExp1.test(IBMSL_domainName) || domainRegExp2.test(IBMSL_domainName) || !domainRegExp3.test(IBMSL_domainName) || !domainRegExp4.test(IBMSL_domainName)) {
                    	    this.addOptionError('domainNameBadParam', 'IBMSL_domainName');
                        isValid = false;
                    }
            }.bind(this),
                
                
            IBMSL_maxMemory : function () {
                    var IBMSL_maxMemory = this._image['IBMSL_maxMemory'];
                    if (!IBMSL_maxMemory || IBMSL_maxMemory === '' || IBMSL_maxMemory === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_maxMemory');
                        isValid = false;
                    }
           }.bind(this),
                
           IBMSL_maxCores : function () {
                    var IBMSL_maxCores = this._image['IBMSL_maxCores'];
                    if (!IBMSL_maxCores || IBMSL_maxCores === '' || IBMSL_maxCores === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_maxCores');
                        isValid = false;
                    }
           }.bind(this),
                
           IBMSL_diskType : function () {
                    var IBMSL_diskType = this._image['IBMSL_diskType'];
                    if (!IBMSL_diskType || IBMSL_diskType === '' || IBMSL_diskType === undefined) {
                        this.addOptionError('notSelected', 'IBMSL_diskType');
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
            	   
           IBMSL_vsiBilling : function () {
         			  var IBMSL_vsiBilling = this._image['IBMSL_vsiBilling'];
                    if (!IBMSL_vsiBilling || IBMSL_vsiBilling === '' || IBMSL_vsiBilling === undefined) {
 	                    this.addOptionError('notSelected', 'IBMSL_vsiBilling');
 	                    isValid = false;
                    }
           }.bind(this),
             	               	   
            	   
           agent_pool_id : function () {
            		   var agentPoolId = this._image['agent_pool_id'];
            		   if (!agentPoolId || agentPoolId === '' || agentPoolId === undefined) {
            		        this.addOptionError('notSelected', 'agent_pool_id');
            		        isValid = false;
            		   }
           }.bind(this),
            	 
           IBMSL_maximumInstances : function () {
        	   var IBMSL_maximumInstances = this._image['IBMSL_maximumInstances'];
        	// RegExp checks positive integer or whitespace
        	   var maximumInstancesRegExp = new RegExp(/^(\s*|[1-9]\d*)$$/,'g');
        	   if (IBMSL_maximumInstances && IBMSL_maximumInstances != undefined
        			   && !maximumInstancesRegExp.test(IBMSL_maximumInstances)) {
        		   isValid = false;
        	   }
           }.bind(this)

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
    	
    		// Add newly created cloud image
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
    	
    		// Handles edit request on cloud image row.
       // this._image['source-id'] = id;
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
    	
    		// Opens delete image dialog box and sends GET ajax request to SoftlayerDeleteCloudImageController.java
        var imageId = $elem.parents(this.selectors.imagesTableRow).data('image-id');

        BS.ajaxUpdater($("softlayerDeleteImageDialogBody"), BS.IBMSoftlayer.DeleteImageDialog.url + window.location.search, {
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
    		
    		// Confirmation on deleting cloud image and sends POST ajax request to SoftlayerDeleteCloudImageController.java
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
    		
    		//disable AddImage button
    		this._toggleShowAddImageDialogButton(false);
    		
    		// Check connection function will send POST ajax request to SoftlayerEditProfileController.java to authenticate user.
	    var valid =	this.validateServerSettings();
	    var $fetchOptions = $j('#error_fetch_options');
	    var $loader = $j('.options-loader');
	    var $vsiTemplateOptions = $j('#IBMSL_vsiTemplate');
	    var $datacenterOptions = $j('#IBMSL_datacenter');
	    var propertiesBeanVsiTemplate = this.propertiesBeanVsiTemplate;
	    var propertiesBeanDatacenter = this.propertiesBeanDatacenter;
	    
	    // if username and apikey are entered
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
	            
	            // Check for any Softlayer api error encountered
	            $err = $response.find('error');
	            if ($err.length) {
	            		
	            	 	$response.find('error').each(function(){
	 	             	$fetchOptions.text($j(this).text());
	 	            	});
	            }
	            else
	            	{
	            		// Username and apikey are correct, hence enable AddImage button
	            		this._toggleShowAddImageDialogButton(true); 
	            		
		            // load vsi template list in select option
		            $response.find('VsiPrivateTemplate').each(function(){
		            		$fetchOptions.text('');
		            		
		            		//creating json for vsiTemplate with imageName and GlobalIdentifier values
		            		var vsiTemplateName = $j(this).attr('name').replace(/ /g, '&nbsp;');
		            		var vsiTemplateJson = {"name":vsiTemplateName , "value":$j(this).attr('id')};
		            		$vsiTemplateOptions.append('<option ' + (($j(this).attr('id') == propertiesBeanVsiTemplate)?'selected = "selected"':'') + ' value='+JSON.stringify(vsiTemplateJson)+'>'+$j(this).attr('name')+'</option>');
		            });
		            
		            // load datacenter list in select option
		            $response.find('Datacenter').each(function(){
		            		$datacenterOptions.append('<option ' + (($j(this).attr('name') == propertiesBeanDatacenter)?'selected = "selected"':'') + ' value="'+$j(this).attr('name')+'">'+$j(this).attr('longName')+'</option>');
		            });
	            	}
	            }.bind(this)
	        });
	    }
    },
    
    validateServerSettings: function() {
    		
    		// Function will check if user has entered username and api key. If not, if will show error.
	    	var isValid = true;
	    	[this.$IBMSL_username, this.$IBMSL_apiKey ].forEach(function($elem) {
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
        return $('softlayerImageDialog');
    }
});

if(!BS.IBMSoftlayer.DeleteImageDialog) BS.IBMSoftlayer.DeleteImageDialog = OO.extend(BS.AbstractModalDialog, {
    url: '',
    currentImageId: '',

    getContainer: function() {
        return $('softlayerDeleteImageDialog');
    },

    show: function (imageId) {
        BS.IBMSoftlayer.DeleteImageDialog.currentImageId = imageId;
        BS.IBMSoftlayer.DeleteImageDialog.showCentered();
}
});