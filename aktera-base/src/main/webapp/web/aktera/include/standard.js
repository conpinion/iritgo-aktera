var onBodyLoads = new Array ();

function registerBodyLoadFunction (func)
{
	onBodyLoads.push (func);
}


function bodyOnLoad () 
{
	history.go (+1);

	for (var i = 0; i < onBodyLoads.length; i++)
	{
		onBodyLoads[i]();
	}
}

function elementPosX (obj)
{
	var curleft = 0;
	if (obj.offsetParent)
	{
		while (1) 
		{
			curleft += obj.offsetLeft;
			if (!obj.offsetParent)
			{
				break;
			}
			obj = obj.offsetParent;
		}
	}
	else if (obj.x)
	{
		curleft += obj.x;
	}
	return curleft;
}	

function elementPosY (obj)
{
	var curtop = 0;
	if (obj.offsetParent)
	{
		while(1)
		{
			curtop += obj.offsetTop;
			if (!obj.offsetParent)
			{
				break;
			}
			obj = obj.offsetParent;
		}
	}
	else if (obj.y)
	{
		curtop += obj.y;
	}
	return curtop;
}

function mouseXFromEvent (event) 
{
	if (event.pageX)
	{
		return event.pageX;
	}
	else if (event.clientX)
	{		
		return event.clientX + (document.documentElement.scrollLeft ?
			document.documentElement.scrollLeft : document.body.scrollLeft);
	}
	else return null;
}

function mouseYFromEvent (event) 
{
	if (event.pageY) 
	{
		return event.pageY;
	}
	else if (event.clientY)
	{
		return event.clientY + (document.documentElement.scrollTop ?
			document.documentElement.scrollTop : document.body.scrollTop);
	}
	
	else return null;
}

