function showPage (pageNum)
{
	document.forms[0].AKTERA_auto.value=true;
	document.forms[0].AKTERA_page.value=pageNum;
	document.forms[0].COMMAND_save.click();
}

function comboBoxSubmit ()
{
	document.forms[0].AKTERA_auto.value=true;
	document.forms[0].COMMAND_save.click();
}
