<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ include file="/aktera/templates/layout.jsp"%>
<%@ include file="/aktera/templates/box.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="head" type="string">
		<bean:define id="urlBase" type="java.lang.String">
			<bean:write name="player" property="attributes.urlBase" />
		</bean:define>
		<bean:define id="server" type="java.lang.String">
			<bean:write name="player" property="attributes.server" />
		</bean:define>
		<link href="<%= urlBase %>/flvplayer/buttons/sexy-buttons-tute.css" rel="stylesheet" type="text/css">
		 <script src="<%= urlBase %>/flvplayer/swfobject.js"></script>
		<script src="<%= urlBase %>/flvplayer/combobox/mootools-1.2.4-core-nc.js"></script>
		<script src="<%= urlBase %>/flvplayer/combobox/mootools-1.2.4.4-more.js"></script>
		<script language="JavaScript" type="text/javascript">
		<!--

		if (navigator.appName.indexOf("Microsoft") == 0)
		{
			function loadEvent(obj, evType, fn)
			{
				if (obj.addEventListener)
				{
					obj.addEventListener(evType, fn, false);
					return (true);
				}
				else if (obj.attachEvent)
				{
					var r = obj.attachEvent("on"+evType, fn);
					return (r);
				}
				else return (false);
			}

			loadEvent(window, "load", function() {

							$('comboBoo').addEvent('change', function(event)
							{
								<logic:iterate id="file" name="list" property="nested">

									<bean:define id="url" type="java.lang.String">
										<bean:write name="file" property="attributes.url" />
									</bean:define>
									<bean:define id="filename" type="java.lang.String">
										<bean:write name="file" property="attributes.filename" />
									</bean:define>

				 					$(document.getElementById('<%= filename %>')).setStyle ('display', 'none');

								</logic:iterate>

							    var w = $('comboBoo').selectedIndex;
			        			var selected_text = $('comboBoo').options[w].value;

								$(document.getElementById(selected_text)).setStyle ('display', 'inline');
							});

					       });
		}
		else
		{
       	       window.addEvent('domready', function() {
     				   $$('.comboBoo').each(function(el)
		       {
				el.addEvent('change', function(event)
				{
					<logic:iterate id="file" name="list" property="nested">

						<bean:define id="url" type="java.lang.String">
							<bean:write name="file" property="attributes.url" />
						</bean:define>
						<bean:define id="filename" type="java.lang.String">
							<bean:write name="file" property="attributes.filename" />
						</bean:define>

	 					document.getElementById('<%= filename %>').setStyle ('display', 'none');

					</logic:iterate>

				    var w = el.selectedIndex;
        			var selected_text = el.options[w].value;

					var div = document.getElementById(selected_text);
        				div.setStyle ('display', 'inline');
				});

		       });
	       });
		}

		var playervars = {
			contentpath: "<%= urlBase %>",
			video: "/flvplayer/content/demo-video.flv",
			preview: "/flvplayer/content/demo-preview.jpg",
		    skin: "skin-applestyle.swf",
			skincolor: "0x2c8cbd",
			autoplay: true
			// ...
			//see documentation for all the parameters
		};

		var params = { scale: "noscale", allowfullscreen: "true", salign: "tl", bgcolor: "#ffffff", base: "."};
		var attributes = { align: "left" };
//                var swd = new SWFObject ();
//		swd.embedSWF("<%= urlBase %>/flvplayer/flvplayer.swf", "videoCanvas", "1280", "1024", "9.0.28", "<%= urlBase %>/flvplayer/expressInstall.swf", playervars, params, attributes);
		swfobject.embedSWF("<%= urlBase %>/flvplayer/flvplayer.swf", "videoCanvas", "1280", "1024", "9.0.28", "<%= urlBase %>/flvplayer/expressInstall.swf", playervars, params, attributes);

		// Playlist

		function registerVideos ()
				{
					<logic:iterate id="file" name="list" property="nested">

						<bean:define id="url" type="java.lang.String">
							<bean:write name="file" property="attributes.url" />
						</bean:define>
						<bean:define id="filename" type="java.lang.String">
							<bean:write name="file" property="attributes.filename" />
						</bean:define>

						document.getElementById("<%= filename %>").onclick = function()
						{
							var player = swfobject.getObjectById("videoCanvas");
								playervars.video = "<%= filename %>";
								playervars.skincolor = "0x2c8cbd" ,
								player.updatePlayer(playervars);
						};

					</logic:iterate>
	     		};

	     		registerBodyLoadFunction (registerVideos)

		//-->
	    </script>
	</tiles:put>

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/help-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="videos" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
	<tiles:insert beanName="box" flush="false">
		<tiles:put name="content" type="string">
                                <!-- single playlist entry -->
		  <div class="cl_01">Bitte wählen Sie ein Thema aus:<select name="vegetables" id="comboBoo" class="comboBoo">
		         <option value='---'> --- </option>

                     <logic:iterate id="file" name="list" property="nested">

                                        <bean:define id="url" type="java.lang.String">
                                                <bean:write name="file" property="attributes.url" />
                                        </bean:define>
                                        <bean:define id="filename" type="java.lang.String">
                                                <bean:write name="file" property="attributes.filename" />
                                        </bean:define>
                                        <bean:define id="videoName" type="java.lang.String">
                                                <bean:write name="file" property="attributes.videoName" />
                                        </bean:define>
                                        <bean:define id="videoDescription" type="java.lang.String">
                                                <bean:write name="file" property="attributes.description" />
                                        </bean:define>


		         <option value='<%= filename %>'> <%=videoName %> </option>


                                </logic:iterate>
							</select></div><br/><br/><br/>

                     <logic:iterate id="file" name="list" property="nested">

                                        <bean:define id="url" type="java.lang.String">
                                                <bean:write name="file" property="attributes.url" />
                                        </bean:define>
                                        <bean:define id="filename" type="java.lang.String">
                                                <bean:write name="file" property="attributes.filename" />
                                        </bean:define>
                                        <bean:define id="videoName" type="java.lang.String">
                                                <bean:write name="file" property="attributes.videoName" />
                                        </bean:define>
                                        <bean:define id="videoDescription" type="java.lang.String">
                                                <bean:write name="file" property="attributes.description" />
                                        </bean:define>

<div style="display: none" id="<%= filename %>">
<div style="background-color: f48221; border-width:1px;
border-style:solid; border-color:grey; padding:0.5em;
text-align:justify; color: white;">
  <%= videoName %><br/>
  <span style="color: black"><%= videoDescription %></span><br/><br/>
  <div class="clearvid" width="100%"> <a class="buttonvid" href="#" onclick="this.blur(); return false;">
  <span>Play</span></a></div>

</div></br></div>



                                </logic:iterate>

			<!-- player container and a splash image (play button) -->
			<div id="videoCanvas" style="">
			    <p>This content requires the Adobe Flash Player.</p>
				<p>
					<a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" /></a>
	    		</p>
			</div>

			<br clear="all"/>

		</tiles:put>
	</tiles:insert>
	</tiles:put>

</tiles:insert>
