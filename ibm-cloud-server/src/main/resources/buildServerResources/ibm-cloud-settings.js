if (!BS) BS = {};
if (!BS.IBMCloud) BS.IBMCloud = {};

if(!BS.IBMCloud.ProfileSettingsForm) BS.IBMCloud.ProfileSettingsForm = OO.extend(BS.PluginPropertiesForm, {
	// Global variables
    checkConnectionUrl: '',
    propertiesBeanVsiTemplate: '',
    propertiesBeanDatacenter: '',
    hasCloudImageEdited: false,
    isEditImageOrAddImageClicked: false,
    customizeMachineTypeChecked: false,
    
    _dataKeys: [ 'IBMSL_vsiTemplate', 'IBMSL_datacenter', 'IBMSL_agentName', 'IBMSL_domainName', 'IBMSL_customizeMachineType', 'IBMSL_flavorList', 'IBMSL_maxMemory', 'IBMSL_maxCores', 'IBMSL_diskType', 'IBMSL_diskSize','IBMSL_network', 'IBMSL_vsiBilling', 'agent_pool_id', 'IBMSL_maximumInstances'],
    
    templates: {
    		// Template for cloud image table row.
        imagesTableRow: $j('<tr class="imagesTableRow">\
        		<td class="IBMSL_vsiTemplate"></td>\
        		<td class="IBMSL_datacenter"></td>\
        		<td class="IBMSL_agentName"></td>\
        		<td class="IBMSL_domainName"></td>\
			<td class="IBMSL_network"></td>\
        		<td class="IBMSL_vsiBilling"></td>\
			<td class="edit"><a href="#" class="editVmImageLink">edit</a></td>\
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
        		IBMSL_customizeMachineType: '!OPTIONAL_FIELD!',
        		IBMSL_flavorList: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_maxMemory: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_maxCores: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_diskType: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_diskSize: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_network: '!SHOULD_NOT_BE_EMPTY!',
        		IBMSL_vsiBilling: '!SHOULD_NOT_BE_EMPTY!'
        },

        _errors: {
        		// Validation error messages
        	 	agentNameBadParam: 'Not a valid Agent Name',
        		domainNameBadParam: 'Not a valid Domain Name',
            required: 'This field cannot be blank',
            notSelected: 'Something should be selected',
            maximumInstances: 'Must be a non-negative integer or leave blank'
        },
        
    _displayedErrors: {},
        
    initialize: function(){
         this.$IBMSL_username = $j(BS.Util.escapeId('IBMSL_username'));
         this.$IBMSL_apiKey = $j(BS.Util.escapeId('secure:IBMSL_apiKey'));
         this.$IBMSL_instanceNumber = $j('#IBMSL_instanceNumber');
         
         this.$imagesTable = $j('#ibmImagesTable');
         this.$imagesTableWrapper = $j('.imagesTableWrapper');
         this.$emptyImagesListMessage = $j('.emptyImagesListMessage'); //TODO: implement
         this.$showAddImageDialogButton = $j('#showAddImageDialogButton');

         // add/edit image dialog
         this.$addImageButton = $j('#ibmAddImageButton');
         this.$cancelAddImageButton = $j('#ibmCancelAddImageButton');
         this.$deleteImageButton = $j('#ibmDeleteImageButton');
         this.$cancelDeleteImageButton = $j('#ibmCancelDeleteImageButton');

         this.$IBMSL_vsiTemplate = $j('#IBMSL_vsiTemplate');
         this.$IBMSL_datacenter = $j('#IBMSL_datacenter');
         this.$IBMSL_agentName = $j('#IBMSL_agentName');
         this.$IBMSL_domainName = $j('#IBMSL_domainName');
         this.$IBMSL_customizeMachineType = $j('#IBMSL_customizeMachineType');
         this.$IBMSL_flavorList = $j('#IBMSL_flavorList');
         this.$IBMSL_maxMemory = $j('#IBMSL_maxMemory');
         this.$IBMSL_maxCores = $j('#IBMSL_maxCores');
         this.$IBMSL_diskType = $j('#IBMSL_diskType');
         this.$IBMSL_diskSize = $j('#IBMSL_diskSize');
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
         
         // disable edit/delete buttons on cloud image table rows, until user is authenticated.
 		this._toggleActionImageButton(false);

         BS.Clouds.Admin.CreateProfileForm.checkIfModified();
         
    },
    
    _toggleShowAddImageDialogButton: function (enable) {
        
    		// Toggling disable attribute on ShowAddImageDialogButton
    		this.$showAddImageDialogButton.attr('disabled', !enable);
    },
    
    _toggleActionImageButton: function (enable) {
    		
    		// Toggle the edit/delete button on image table rows.
        if(enable) {
	        	this.$imagesTable.find('tr').each(function() {
	    			$j(this).find('.edit .editVmImageLink').removeClass('disableActionButton');
	    			$j(this).find('.remove .removeVmImageLink').removeClass('disableActionButton');
	    		});
        }
        else {
        		this.$imagesTable.find('tr').each(function() {
        			$j(this).find('.edit .editVmImageLink').addClass('disableActionButton');
        			$j(this).find('.remove .removeVmImageLink').addClass('disableActionButton');
        		});
        }
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
        /* This is used to add edit event on all 'td' in row. Where ever on table row you click, it will call edit image function.
         * var editDelegates = this.selectors.imagesTableRow + ' .highlight, ' + this.selectors.editImageLink;*/
        var editDelegates = this.selectors.editImageLink;
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
        
        // On Change event for fields.
        this.$IBMSL_vsiTemplate.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_vsiTemplate.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_vsiTemplate'], this.$IBMSL_vsiTemplate.val());
            this._image['IBMSL_vsiTemplate'] = this.$IBMSL_vsiTemplate.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_datacenter.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_datacenter.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_datacenter'], this.$IBMSL_datacenter.val());
            this._image['IBMSL_datacenter'] = this.$IBMSL_datacenter.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_agentName.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_agentName.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_agentName'], this.$IBMSL_agentName.val());
            this._image['IBMSL_agentName'] = this.$IBMSL_agentName.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_domainName.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_domainName.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_domainName'], this.$IBMSL_domainName.val());
            this._image['IBMSL_domainName'] = this.$IBMSL_domainName.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_customizeMachineType.on('change', function (e, value) {
        		if(value !== undefined) {
        			if(value == 'true' || value === true) this.$IBMSL_customizeMachineType.prop('checked', true);
        	        else this.$IBMSL_customizeMachineType.prop('checked',false);
        		}
        	    this._image['IBMSL_customizeMachineType'] = this.$IBMSL_customizeMachineType.is(':checked');
        	    this.customizeMachineTypeChecked = this.$IBMSL_customizeMachineType.is(':checked');
            if(this.customizeMachineTypeChecked) {
        	    		$j('.customizeMachine').removeClass('hidden');
        	    		$j('.flavor').addClass('hidden');
        	    		this._image['IBMSL_flavorList'] = '';
        	    	}
        	    	else {
        	    		$j('.customizeMachine').addClass('hidden');
        	    		$j('.flavor').removeClass('hidden');
        	    		this._image['IBMSL_maxMemory'] = '';
        	    		this._image['IBMSL_maxCores'] = '';
        	    		this._image['IBMSL_diskType'] = '';
        	    		this._image['IBMSL_diskSize'] = '';
        	    	}
        	    	this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_flavorList.on('change', function (e, value) {
        		if(value !== undefined) this.$IBMSL_flavorList.val(value);
        		this.checkCloudImageEdited(this._image['IBMSL_flavorList'], this.$IBMSL_flavorList.val());
        		this._image['IBMSL_flavorList'] = this.$IBMSL_flavorList.val();
        		this.validateOptions(e.target.getAttribute('data-id'));
        	}.bind(this));
        
        this.$IBMSL_maxMemory.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_maxMemory.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_maxMemory'], this.$IBMSL_maxMemory.val());
            this._image['IBMSL_maxMemory'] = this.$IBMSL_maxMemory.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_maxCores.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_maxCores.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_maxCores'], this.$IBMSL_maxCores.val());
            this._image['IBMSL_maxCores'] = this.$IBMSL_maxCores.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_diskType.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_diskType.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_diskType'], this.$IBMSL_diskType.val());
            this._image['IBMSL_diskType'] = this.$IBMSL_diskType.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_diskSize.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_diskSize.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_diskSize'], this.$IBMSL_diskSize.val());
            this._image['IBMSL_diskSize'] = this.$IBMSL_diskSize.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_network.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_network.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_network'], this.$IBMSL_network.val());
            this._image['IBMSL_network'] = this.$IBMSL_network.val();
            this.validateOptions(e.target.getAttribute('data-id'));
        }.bind(this));
        
        this.$IBMSL_vsiBilling.on('change', function (e, value) {
            if(value !== undefined) this.$IBMSL_vsiBilling.val(value);
            this.checkCloudImageEdited(this._image['IBMSL_vsiBilling'], this.$IBMSL_vsiBilling.val());
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
        }.bind(this));
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
    		// Toggle the cloud image table. If no image present hide else show.
        var toggle = !!this._imagesDataLength;
        this.$imagesTableWrapper.removeClass('hidden');
        this.$emptyImagesListMessage.toggleClass('hidden', toggle);
        this.$imagesTable.toggleClass('hidden', !toggle);
    },
    
    _renderImageRow: function (props, id) {
    		
    		//This function creates each row of cloud image table and appends.
        var $row = this.templates.imagesTableRow.clone().attr('data-image-id', id);
        $row.attr('src-id', props['source-id']);
        var defaults = this.defaults;

        this._dataKeys.forEach(function (className) {
        		if(className == 'IBMSL_vsiTemplate')
        			{
        				var vsiTemplateValueJson = JSON.parse(props[className]);
        				$row.find('.' + className).text(vsiTemplateValueJson.name.replace(/&nbsp;/g,' ') || defaults[className]);
        			}
        		else if(className == 'IBMSL_vsiBilling')
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
            this.clearOptionsErrors();
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
            BS.IBMCloud.ImageDialog.close();
        }
        return false;
    },

    _cancelDialogClickHandler: function () {
        BS.IBMCloud.ImageDialog.close();
        return false;
    },
    
    showAddImageDialog: function () {
    		// AddImage button is clicked, set variable isEditImageOrAddImageClicked to false. 
    		this.isEditImageOrAddImageClicked = false;
    		$j('#imageChangeMesssage').addClass('hidden');
    		
        $j('#ibmImageDialogTitle').text('Add IBM Cloud Image');
        BS.Hider.addHideFunction('ibmImageDialog', this._resetDataAndDialog.bind(this));
        this.$addImageButton.val('Add').data('image-id', 'undefined');
        // initializing _image with fields default value.
        this._image = {};
        this._image['IBMSL_vsiTemplate'] = "";
        	this._image['IBMSL_datacenter'] = "";
        	this._image['IBMSL_agentName'] = "";
        	this._image['IBMSL_domainName'] = "";
        	this._image['IBMSL_datacenter'] = "";
        	this._image['IBMSL_customizeMachineType'] = false;
        	this._image['IBMSL_flavorList'] = "";
        	this._image['IBMSL_maxMemory'] = "";
        	this._image['IBMSL_maxCores'] = "";
        	this._image['IBMSL_diskType'] = "";
        	this._image['IBMSL_diskSize'] = "";
        	this._image['IBMSL_network'] = "";
        	this._image['IBMSL_vsiBilling'] = "";
        	this._image['agent_pool_id'] = "";
        	this._image['IBMSL_maximumInstances'] = "";
        BS.IBMCloud.ImageDialog.showCentered();
    },
    
    showEditImageDialog: function ($elem) {
    		/* Opens the edit image dialog box
    		 * EditImage button is clicked, set variable 'isEditImageOrAddImageClicked' to true. 
    		 */
    		this.isEditImageOrAddImageClicked = true;
    		this.hasCloudImageEdited = false;
    		$j('#imageChangeMesssage').addClass('hidden');
    		
        var imageId = $elem.parents(this.selectors.imagesTableRow).data('image-id');
        var srcId = $elem.parents(this.selectors.imagesTableRow).attr('src-id');

        $j('#ibmImageDialogTitle').text('Edit IBM Cloud Image');

        BS.Hider.addHideFunction('ibmImageDialog', this._resetDataAndDialog.bind(this));

        typeof imageId !== 'undefined' && (this._image = $j.extend({}, this.imagesData[imageId]));
        this.$addImageButton.val('Save').data('image-id', imageId);
        if (imageId === 'undefined'){
            this.$addImageButton.removeData('image-id');
        }
        
        // ajax call to delete controller to get list of running VSis on image. We get HTML response in resonseText object.
        BS.ajaxUpdater($("ibmDeleteImageDialogBody"), BS.IBMCloud.DeleteImageDialog.url + window.location.search, {
            method: 'get',
            parameters : {
                imageId : srcId
            },
            onSuccess: function(response) {
	            	var $response = $j(response.responseText);
	            	if($response[2] === 'undefined' || $response[2] === undefined || Object.keys($response).length <= 2) {
	            		$j('#imageChangeMesssage i').html("Saving the changes will provision new instance and there are no running instance on this image.");
	            	}
	            	else {
	            		$j('#imageChangeMesssage i').html("Saving the changes will provision new instance and terminate following cloud instance(s):<ul> "+$response[2].innerHTML+"</ul>");
	            	}
            }
        });

        var image = this._image;
        
        this.$IBMSL_vsiTemplate.trigger('change', image['IBMSL_vsiTemplate'] || '');
        this.$IBMSL_datacenter.trigger('change', image['IBMSL_datacenter'] || '');
        this.$IBMSL_agentName.trigger('change', image['IBMSL_agentName'] || '');
        this.$IBMSL_domainName.trigger('change', image['IBMSL_domainName'] || '');
        this.$IBMSL_customizeMachineType.trigger('change', image['IBMSL_customizeMachineType'] || '');
        this.$IBMSL_flavorList.trigger('change', image['IBMSL_flavorList'] || '');
        this.$IBMSL_maxMemory.trigger('change', image['IBMSL_maxMemory'] || '');
        this.$IBMSL_maxCores.trigger('change', image['IBMSL_maxCores'] || '');
        this.$IBMSL_diskType.trigger('change', image['IBMSL_diskType'] || '');
        this.$IBMSL_diskSize.trigger('change', image['IBMSL_diskSize'] || '');
        this.$IBMSL_network.trigger('change', image['IBMSL_network'] || '');
        this.$IBMSL_vsiBilling.trigger('change', image['IBMSL_vsiBilling'] || '');
        this.$agentPoolId.trigger('change', image['agent_pool_id'] || '');
        this.$IBMSL_maximumInstances.trigger('change', image['IBMSL_maximumInstances'] || '');

        BS.IBMCloud.ImageDialog.showCentered();
    },

    _resetDataAndDialog: function () {
        this._image = {};

        this.$IBMSL_vsiTemplate.trigger('change', '');
        this.$IBMSL_datacenter.trigger('change', '');
        this.$IBMSL_agentName.trigger('change', '');
        this.$IBMSL_domainName.trigger('change', '');
        this.$IBMSL_customizeMachineType.trigger('change', '');
        this.$IBMSL_flavorList.trigger('change', '');
        this.$IBMSL_maxMemory.trigger('change', '');
        this.$IBMSL_maxCores.trigger('change', '');
        this.$IBMSL_diskType.trigger('change', '');
        this.$IBMSL_diskSize.trigger('change', '');
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
                    if (!this._image['IBMSL_domainName'] || this._image['IBMSL_domainName'] === '' || this._image['IBMSL_domainName'] === undefined) {
                      this._image['IBMSL_domainName'] = 'default.com';
                    }
                    var IBMSL_domainName = this._image['IBMSL_domainName'];
                    /* domainRegExp1 checks:
                    	* 1) Each alphanumeric string separated by a period is considered a label. The last label, the TLD (top level domain).
                    	* 2) The domain portion must consist of least one label followed by a period '.' then ending with the TLD label.
                    	* 3) Labels must begin and end with an alphanumeric character.
                    	* 4) Label can use '-'. And '-' & '.' shall not be adjacent.
                    	* 5) TLD must not contain any digit or '-'.
                    	* */
                    //var domainRegExp1 = new RegExp(/^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)*(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)$/,'g');
                		var domainRegExp1 = new RegExp(/^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)*(\.[A-Za-z]+)$/,'g');
                                       
                    	/* domainRegExp2 checks:
                    	* 1) TLd is not solely comprised of only digits.
                    	* Test: True -> TLD only contains digits.
                    	* */
                    	//var domainRegExp2 = new RegExp(/^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)(\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)*(\.[0-9]+(-[0-9]+)*)$/,'g');
                                      
                   	/* domainRegExp3 checks:
                    	* 1) Length of each Label is min:1 to max:63
                    	* 2) The last label, the TLD (top level domain) must be between 2-24 alphabetic characters.
                    	* */
                     var domainRegExp3 = new RegExp(/^([A-Za-z0-9-]{1,63})((\.[A-Za-z0-9-]{1,63}){0,})(\.[A-Za-z]{2,23})$/,'g'); 
                                       
                    	/* domainRegExp4 checks:
                    	* 1) Combining the agentname, followed by a period '.', followed by the domain gives the FQDN (fully qualified domain name), 
                    	* which may not exceed 253 characters in total length.
                    	* 2) String (including '.') length is min:2 to max:189 (253-63-1 = 189 [253-agentnNameLength-periodFollowedByAgentname])
                    	* */
                    	var domainRegExp4 = new RegExp(/^.{2,189}$/,'g'); 
                    	
                    	if (!IBMSL_domainName || IBMSL_domainName === '' || IBMSL_domainName === undefined || !domainRegExp1.test(IBMSL_domainName) || !domainRegExp3.test(IBMSL_domainName) || !domainRegExp4.test(IBMSL_domainName)) {
                    	    this.addOptionError('domainNameBadParam', 'IBMSL_domainName');
                        isValid = false;
                    }
            }.bind(this),
            
            IBMSL_flavorList : function () {
            		var IBMSL_flavorList = this._image['IBMSL_flavorList'];
            	    	if (!this.customizeMachineTypeChecked && (!IBMSL_flavorList || IBMSL_flavorList === '' || IBMSL_flavorList === undefined)) {
            	    		this.addOptionError('notSelected', 'IBMSL_flavorList');
            	    		isValid = false;
            	    	}
            }.bind(this),
                
            IBMSL_maxMemory : function () {
                    var IBMSL_maxMemory = this._image['IBMSL_maxMemory'];
                    if (this.customizeMachineTypeChecked && (!IBMSL_maxMemory || IBMSL_maxMemory === '' || IBMSL_maxMemory === undefined)) {
                        this.addOptionError('notSelected', 'IBMSL_maxMemory');
                        isValid = false;
                    }
           }.bind(this),
                
           IBMSL_maxCores : function () {
                    var IBMSL_maxCores = this._image['IBMSL_maxCores'];
                    if (this.customizeMachineTypeChecked && (!IBMSL_maxCores || IBMSL_maxCores === '' || IBMSL_maxCores === undefined)) {
                        this.addOptionError('notSelected', 'IBMSL_maxCores');
                        isValid = false;
                    }
           }.bind(this),
                
           IBMSL_diskType : function () {
                    var IBMSL_diskType = this._image['IBMSL_diskType'];
                    if (this.customizeMachineTypeChecked && (!IBMSL_diskType || IBMSL_diskType === '' || IBMSL_diskType === undefined)) {
                        this.addOptionError('notSelected', 'IBMSL_diskType');
                        isValid = false;
                    }
           }.bind(this),
           
           IBMSL_diskSize : function () {
               var IBMSL_diskSize = this._image['IBMSL_diskSize'];
               if (this.customizeMachineTypeChecked && (!IBMSL_diskSize || IBMSL_diskSize === '' || IBMSL_diskSize === undefined)) {
                   this.addOptionError('notSelected', 'IBMSL_diskSize');
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
		        	   // RegExp checks non-negative integer or whitespace
		        	   var maximumInstancesRegExp = new RegExp(/^(\s*|\d*)$/,'g');
		        	   if (IBMSL_maximumInstances && IBMSL_maximumInstances != undefined
		        			   && !maximumInstancesRegExp.test(IBMSL_maximumInstances)) {
		        		   this.addOptionError('maximumInstances', 'IBMSL_maximumInstances');
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
    		
    		// Print validation error message to fields.
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
    		// Clear the options validation  errors.
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
    		// Returns sequetial IDs. TODO: Consider changing so it returns random IDs.
        if($j.isEmptyObject(this.imagesData)) return 1;
        else return Math.max.apply(Math, $j.map(this.imagesData, function callback(currentValue) {
            return currentValue['source-id'];
        })) + 1;
    },
    
    checkCloudImageEdited: function (currentValue, newValue) {
    		
    		// check if value is changed and check if it was changed while 'edit' event.
    		if(currentValue != newValue && this.isEditImageOrAddImageClicked) {
    			$j('#imageChangeMesssage').removeClass('hidden');
    			this.hasCloudImageEdited = true;
    		}
    },
    
    editImage: function (id) {
    		// Handles edit request on cloud image row.
    		// this._image['source-id'] = id;

    		$j('#imageChangeMesssage').addClass('hidden');
        if(this.hasCloudImageEdited) {
        	
        			var oldSrc = this._image['source-id'];
        			BS.ajaxRequest(BS.IBMCloud.DeleteImageDialog.url + window.location.search, {
        	            method: 'post',
        	            parameters : {
        	                imageId : oldSrc
        	            },
        	            onComplete: function() {
        	            }
        	        });
        }
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
    	
    		// Opens delete image dialog box and sends GET ajax request to IBMDeleteCloudImageController.java
        var imageId = $elem.parents(this.selectors.imagesTableRow).data('image-id');
        var srcId = $elem.parents(this.selectors.imagesTableRow).attr('src-id');

        BS.ajaxUpdater($("ibmDeleteImageDialogBody"), BS.IBMCloud.DeleteImageDialog.url + window.location.search, {
            method: 'get',
            parameters : {
                imageId : srcId
            },
            onComplete: function() {
                BS.IBMCloud.DeleteImageDialog.show(imageId, srcId);
            }
        });
    },
    
    _cancelDeleteImageDialogClickHandler: function () {
        BS.IBMCloud.DeleteImageDialog.close();
        return false;
    },

    _submitDeleteImageDialogClickHandler: function() {
    		
    		// Confirmation on deleting cloud image and sends POST ajax request to IBMDeleteCloudImageController.java
        var imageId = BS.IBMCloud.DeleteImageDialog.currentImageId;
        var srcId = BS.IBMCloud.DeleteImageDialog.currentSrcId;
        var $deleteLoader = $j('.delete-loader');
        $deleteLoader.removeClass('hidden');
        
        BS.ajaxRequest(BS.IBMCloud.DeleteImageDialog.url + window.location.search, {
            method: 'post',
            parameters : {
                imageId : srcId
            },
            onComplete: function() {
                BS.IBMCloud.ProfileSettingsForm.removeImage(imageId);
                $deleteLoader.addClass('hidden');
                BS.IBMCloud.DeleteImageDialog.close();
            }
        });
    },
    
    checkConnection: function() {
    		
    		//disable AddImage button
    		this._toggleShowAddImageDialogButton(false);
    		
    		//disable edit/delete buttons on cloud image table rows.
    		this._toggleActionImageButton(false);
    		
    		// Check connection function will send POST ajax request to IBMEditProfileController.java to authenticate user.
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
	 	             	//$fetchOptions.text($j(this).text());
	            	 		$fetchOptions.text("Invalid Credentials: Try entering valid Username and API.");
	            	 	});
	            }
	            else
	            	{
	            		// Username and apikey are correct, hence enable AddImage button
	            		this._toggleShowAddImageDialogButton(true); 
	            		
	            		// Enable edit/delete buttons on cloud image table rows.
	            		this._toggleActionImageButton(true);
	            		
	            		
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

if(!BS.IBMCloud.ImageDialog) BS.IBMCloud.ImageDialog = OO.extend(BS.AbstractModalDialog, {
    getContainer: function() {
        return $('ibmImageDialog');
    }
});

if(!BS.IBMCloud.DeleteImageDialog) BS.IBMCloud.DeleteImageDialog = OO.extend(BS.AbstractModalDialog, {
    url: '',
    currentImageId: '',
    currentSrcId: '',

    getContainer: function() {
        return $('ibmDeleteImageDialog');
    },

    show: function (imageId, srcId) {
        BS.IBMCloud.DeleteImageDialog.currentImageId = imageId;
        BS.IBMCloud.DeleteImageDialog.currentSrcId = srcId;
        BS.IBMCloud.DeleteImageDialog.showCentered();
}
});
