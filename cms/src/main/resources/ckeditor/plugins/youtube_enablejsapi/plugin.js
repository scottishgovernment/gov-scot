/*
* Youtube Embed Plugin
*
* @author Jonnas Fonini <jonnasfonini@gmail.com>
* @version 2.1.18
*/
(function () {
	CKEDITOR.plugins.add('youtube_enablejsapi', {
		lang: [ 'en', 'bg', 'pt', 'pt-br', 'ja', 'hu', 'it', 'fr', 'tr', 'ru', 'de', 'ar', 'nl', 'pl', 'vi', 'zh', 'el', 'he', 'es', 'nb', 'nn', 'fi', 'et', 'sk', 'cs', 'ko', 'eu', 'uk'],
		init: function (editor) {
			editor.addCommand('youtube_enablejsapi', new CKEDITOR.dialogCommand('youtube_enablejsapi', {
				allowedContent: 'div{*}(*)[data-*]; iframe{*}[!width,!height,!src,!frameborder,!allowfullscreen,!allow]; object param[*]; a[*]; img[*]'
			}));

			editor.ui.addButton('Youtube', {
				label : editor.lang.youtube.button,
				toolbar : 'insert',
				command : 'youtube_enablejsapi',
				icon : this.path + 'images/icon.png'
			});

			CKEDITOR.dialog.add('youtube_enablejsapi', function (instance) {
				var video,
					disabled = editor.config.youtube_disabled_fields || [];

				function handleLinkChange(el, api) {
					var video = ytVidId(el.getValue());
					var time = ytVidTime(el.getValue());

					if (video && time) {
						var seconds = timeParamToSeconds(time);
						var hms = secondsToHms(seconds);
						el.getDialog().getContentElement('youtubeEnableJsApiPlugin', 'txtStartAt').setValue(hms);
					}
				}

				return {
					title : editor.lang.youtube.title,
					minWidth : 510,
					minHeight : 200,
					onShow: function () {
						for (var i = 0; i < disabled.length; i++) {
							this.getContentElement('youtubeEnableJsApiPlugin', disabled[i]).disable();
						}
					},
					contents :
						[{
							id : 'youtubeEnableJsApiPlugin',
							expand : true,
							elements :
								[{
									type : 'hbox',
									widths : [ '70%', '15%', '15%' ],
									children :
									[
										{
											id : 'txtUrl',
											type : 'text',
											label : editor.lang.youtube.txtUrl,
											onChange : function (api) {
												handleLinkChange(this, api);
											},
											onKeyUp : function (api) {
												handleLinkChange(this, api);
											},
											validate : function () {
												if (this.isEnabled()) {
													if (!this.getValue()) {
														alert(editor.lang.youtube.noCode);
														return false;
													}
													else{
														video = ytVidId(this.getValue());

														if (this.getValue().length === 0 ||  video === false)
														{
															alert(editor.lang.youtube.invalidUrl);
															return false;
														}
													}
												}
											}
										},
										{
											type : 'text',
											id : 'txtWidth',
											width : '60px',
											label : editor.lang.youtube.txtWidth,
											'default' : editor.config.youtube_width != null ? editor.config.youtube_width : '640',
											validate : function () {
												if (this.getValue()) {
													var width = Number(this.getValue());

													if (isNaN(width)) {
														alert(editor.lang.youtube.invalidWidth);
														return false;
													}
												}
												else {
													alert(editor.lang.youtube.noWidth);
													return false;
												}
											}
										},
										{
											type : 'text',
											id : 'txtHeight',
											width : '60px',
											label : editor.lang.youtube.txtHeight,
											'default' : editor.config.youtube_height != null ? editor.config.youtube_height : '360',
											validate : function () {
												if (this.getValue()) {
													var height = Number(this.getValue());

													if (isNaN(height)) {
														alert(editor.lang.youtube.invalidHeight);
														return false;
													}
												}
												else {
													alert(editor.lang.youtube.noHeight);
													return false;
												}
											}
										}
									]
								},
								{
									type : 'hbox',
									widths : [ '55%', '45%' ],
									children :
										[
											{
												id : 'chkResponsive',
												type : 'checkbox',
												label : editor.lang.youtube.txtResponsive,
												'default' : editor.config.youtube_responsive != null ? editor.config.youtube_responsive : false
											},
											{
												id : 'chkNoEmbed',
												type : 'checkbox',
												label : editor.lang.youtube.txtNoEmbed,
												'default' : editor.config.youtube_noembed != null ? editor.config.youtube_noembed : false
											}
										]
								},
								{
									type : 'hbox',
									widths : [ '55%', '45%' ],
									children :
									[
										{
											id : 'chkRelated',
											type : 'checkbox',
											'default' : editor.config.youtube_related != null ? editor.config.youtube_related : true,
											label : editor.lang.youtube.chkRelated
										}
									]
								},
								{
									type : 'hbox',
									widths : [ '55%', '45%' ],
									children :
									[
										{
											id : 'chkPrivacy',
											type : 'checkbox',
											label : editor.lang.youtube.chkPrivacy,
											'default' : editor.config.youtube_privacy != null ? editor.config.youtube_privacy : false
										}
									]
								},
								{
									type : 'hbox',
									widths : [ '55%', '45%'],
									children :
									[
										{
											id : 'txtStartAt',
											type : 'text',
											label : editor.lang.youtube.txtStartAt,
											validate : function () {
												if (this.getValue()) {
													var str = this.getValue();

													if (!/^(?:(?:([01]?\d|2[0-3]):)?([0-5]?\d):)?([0-5]?\d)$/i.test(str)) {
														alert(editor.lang.youtube.invalidTime);
														return false;
													}
												}
											}
										},
										{
											id : 'chkControls',
											type : 'checkbox',
											'default' : editor.config.youtube_controls != null ? editor.config.youtube_controls : true,
											label : editor.lang.youtube.chkControls
										}
									]
								}
							]
						}
					],
					onOk: function()
					{
						var content = '';
						var responsiveStyle = '';

						var url = 'https://', params = [], startSecs, paramAutoplay='';
						var width = this.getValueOf('youtubeEnableJsApiPlugin', 'txtWidth');
						var height = this.getValueOf('youtubeEnableJsApiPlugin', 'txtHeight');

						if (this.getContentElement('youtubeEnableJsApiPlugin', 'chkPrivacy').getValue() === true) {
							url += 'www.youtube-nocookie.com/';
						}
						else {
							url += 'www.youtube.com/';
						}

						url += 'embed/' + video;

						if (this.getContentElement('youtubeEnableJsApiPlugin', 'chkRelated').getValue() === false) {
							params.push('rel=0');
						}

						if (this.getContentElement('youtubeEnableJsApiPlugin', 'chkControls').getValue() === false) {
							params.push('controls=0');
						}

						params.push('enablejsapi=1');

						startSecs = this.getValueOf('youtubeEnableJsApiPlugin', 'txtStartAt');

						if (startSecs) {
							var seconds = hmsToSeconds(startSecs);

							params.push('start=' + seconds);
						}

						if (params.length > 0) {
							url = url + '?' + params.join('&');
						}

						var imgSrc = 'https://img.youtube.com/vi/' + video + '/sddefault.jpg';

						var videoWrapperClasses = ['youtube-embed-wrapper'];

						if (this.getContentElement('youtubeEnableJsApiPlugin', 'chkResponsive').getValue() === true) {
							videoWrapperClasses.push('youtube-embed-wrapper--responsive');
						}

						content += '<div class="' + videoWrapperClasses.join('  ') + '" data-src="' + url + '" data-width="' + width + '" data-height="' + height + '">';

						if (this.getContentElement('youtubeEnableJsApiPlugin', 'chkNoEmbed').getValue() === true) {
							content += '<a href="' + url + '" ><img width="' + width + '" height="' + height + '" src="' + imgSrc + '" '  + responsiveStyle + '/></a>';
						}
						else {
							content += '<a href="' + url + '" ><img width="' + width + '" height="' + height + '" src="' + imgSrc + '" '  + responsiveStyle + '/></a>';
							//content += '<iframe ' + (paramAutoplay ? 'allow="' + paramAutoplay + ';" ' : '') + 'width="' + width + '" height="' + height + '" src="' + url + '" ' + responsiveStyle;
							//content += 'frameborder="0" allowfullscreen></iframe>';
						}

						content += '</div>';

						var element = CKEDITOR.dom.element.createFromHtml(content);
						var instance = this.getParentEditor();
						instance.insertElement(element);
					}
				};
			});
		}
	});
})();

/**
 * JavaScript function to match (and return) the video Id
 * of any valid Youtube Url, given as input string.
 * @author: Stephan Schmitz <eyecatchup@gmail.com>
 * @url: http://stackoverflow.com/a/10315969/624466
 */
function ytVidId(url) {
	var p = /^(?:https?:\/\/)?(?:(?:www|m).)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
	return (url.match(p)) ? RegExp.$1 : false;
}

/**
 * Matches and returns time param in YouTube Urls.
 */
function ytVidTime(url) {
	var p = /t=([0-9hms]+)/;
	return (url.match(p)) ? RegExp.$1 : false;
}

/**
 * Converts time in hms format to seconds only
 */
function hmsToSeconds(time) {
	var arr = time.split(':'), s = 0, m = 1;

	while (arr.length > 0) {
		s += m * parseInt(arr.pop(), 10);
		m *= 60;
	}

	return s;
}

/**
 * Converts seconds to hms format
 */
function secondsToHms(seconds) {
	var h = Math.floor(seconds / 3600);
	var m = Math.floor((seconds / 60) % 60);
	var s = seconds % 60;

	var pad = function (n) {
		n = String(n);
		return n.length >= 2 ? n : "0" + n;
	};

	if (h > 0) {
		return pad(h) + ':' + pad(m) + ':' + pad(s);
	}
	else {
		return pad(m) + ':' + pad(s);
	}
}

/**
 * Converts time in youtube t-param format to seconds
 */
function timeParamToSeconds(param) {
	var componentValue = function (si) {
		var regex = new RegExp('(\\d+)' + si);
		return param.match(regex) ? parseInt(RegExp.$1, 10) : 0;
	};

	return componentValue('h') * 3600
		+ componentValue('m') * 60
		+ componentValue('s');
}

/**
 * Converts seconds into youtube t-param value, e.g. 1h4m30s
 */
function secondsToTimeParam(seconds) {
	var h = Math.floor(seconds / 3600);
	var m = Math.floor((seconds / 60) % 60);
	var s = seconds % 60;
	var param = '';

	if (h > 0) {
		param += h + 'h';
	}

	if (m > 0) {
		param += m + 'm';
	}

	if (s > 0) {
		param += s + 's';
	}

	return param;
}
