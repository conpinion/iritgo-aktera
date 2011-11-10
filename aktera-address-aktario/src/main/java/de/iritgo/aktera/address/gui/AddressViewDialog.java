/**
 * This file is part of the Iritgo/Aktera Framework.
 *
 * Copyright (C) 2005-2011 Iritgo Technologies.
 * Copyright (C) 2003-2005 BueroByte GbR.
 *
 * Iritgo licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iritgo.aktera.address.gui;


import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.swixml.SwingEngine;
import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.gui.IAction;
import de.iritgo.aktario.core.gui.IActionEvent;
import de.iritgo.aktario.core.logger.Log;
import de.iritgo.aktera.address.AddressClientManager;
import de.iritgo.aktera.address.wsclient.Address;
import de.iritgo.aktera.address.wsclient.AddressClientService;
import de.iritgo.aktera.address.wsclient.PhoneNumber;
import de.iritgo.simplelife.process.Procedure1;
import de.iritgo.simplelife.string.StringTools;


@SuppressWarnings("serial")
public class AddressViewDialog
{
	private JFrame frame;

	private JTextField salutation;

	private JTextField firstName;

	private JTextField lastName;

	private JTextField company;

	private JTextField division;

	private JTextField position;

	private JTextField street;

	private JTextField postalCodeAndCity;

	private JTextField country;

	private JTextField email;

	private JTextField homepage;

	private JTextField contactNumber;

	private JTextField companyNumber;

	private JTextArea remark;

	private JTextField phoneNumberB;

	private JPanel phoneNumberBActions;

	private JTextField phoneNumberBDD;

	private JPanel phoneNumberBDDActions;

	private JTextField phoneNumberBF;

	private JPanel phoneNumberBFActions;

	private JTextField phoneNumberBM;

	private JPanel phoneNumberBMActions;

	private JTextField phoneNumberP;

	private JPanel phoneNumberPActions;

	private JTextField phoneNumberPF;

	private JPanel phoneNumberPFActions;

	private JTextField phoneNumberPM;

	private JPanel phoneNumberPMActions;

	private JTextField phoneNumberVOIP;

	private JPanel phoneNumberVOIPActions;

	public Action close = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			frame.setVisible(false);
		}
	};

	public AddressViewDialog()
	{
		try
		{

			AddressClientManager acm = (AddressClientManager) Engine.instance().getManager(AddressClientManager.ID);

			frame = (JFrame) new SwingEngine(this).render(getClass().getResource("/swixml/AddressViewDialog.xml"));

			for (IAction action : acm.getPhoneNumberFieldActions())
			{
				action.addActionInterceptor(new Procedure1<IActionEvent>()
				{
					public void execute(IActionEvent e)
					{
						String command = e.getEvent().getActionCommand();
						String number = "";
						if ("B".equals(command))
						{
							number = phoneNumberB.getText();
						}
						else if ("BM".equals(command))
						{
							number = phoneNumberBM.getText();
						}
						else if ("BF".equals(command))
						{
							number = phoneNumberBF.getText();
						}
						else if ("BDD".equals(command))
						{
							number = phoneNumberBDD.getText();
						}
						else if ("P".equals(command))
						{
							number = phoneNumberP.getText();
						}
						else if ("PM".equals(command))
						{
							number = phoneNumberPM.getText();
						}
						else if ("PF".equals(command))
						{
							number = phoneNumberPF.getText();
						}
						else if ("VOIP".equals(command))
						{
							number = phoneNumberVOIP.getText();
						}
						e.getProperties().setProperty("number", number);
					}
				});
				JButton button = new JButton(action);
				button.setActionCommand("B");
				phoneNumberBActions.add(button);
				button = new JButton(action);
				button.setActionCommand("BM");
				phoneNumberBMActions.add(button);
				button = new JButton(action);
				button.setActionCommand("BF");
				phoneNumberBFActions.add(button);
				button = new JButton(action);
				button.setActionCommand("BDD");
				phoneNumberBDDActions.add(button);
				button = new JButton(action);
				button.setActionCommand("P");
				phoneNumberPActions.add(button);
				button = new JButton(action);
				button.setActionCommand("PM");
				phoneNumberPMActions.add(button);
				button = new JButton(action);
				button.setActionCommand("PF");
				phoneNumberPFActions.add(button);
				button = new JButton(action);
				button.setActionCommand("VOIP");
				phoneNumberVOIPActions.add(button);
			}
		}
		catch (Exception x)
		{
			Log.logError("plugin", "AddressViewDialog", x.toString());
		}
	}

	public void show(final String addressStoreName, final String addressDn)
	{
		show(addressStoreName, addressDn, null);
	}

	public void show(final String addressStoreName, final String addressDn, final String callerInfo)
	{
		if (callerInfo != null)
		{
			frame.setTitle(Engine.instance().getResourceService().getStringWithParams("addressOfCaller", callerInfo));
		}
		else
		{
			frame.setTitle(Engine.instance().getResourceService().getStringWithParams("address"));
		}

		frame.setSize(640, 480);
		frame.setVisible(true);
		new Thread()
		{
			@Override
			public void run()
			{
				final AddressClientService acs = (AddressClientService) Engine.instance().getManager(
								AddressClientService.ID);
				final Address address = acs.getAddress(addressStoreName, addressDn);

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						salutation.setText(Engine.instance().getResourceService().getString(address.getSalutation()));
						firstName.setText(address.getFirstName());
						lastName.setText(address.getLastName());
						company.setText(address.getCompany());
						division.setText(address.getDivision());
						position.setText(address.getPosition());
						street.setText(address.getStreet());
						postalCodeAndCity.setText(address.getPostalCode() + " " + address.getCity());
						country.setText(address.getCountry());
						email.setText(address.getEmail());
						homepage.setText(address.getHomepage());
						contactNumber.setText(address.getContactNumber());
						companyNumber.setText(address.getCompanyNumber());
						homepage.setText(address.getHomepage());
						remark.setText(address.getRemark());
						phoneNumberB.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.B)
										.getDisplayNumber());
						phoneNumberBDD.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.BDD)
										.getDisplayNumber());
						phoneNumberBF.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.BF)
										.getDisplayNumber());
						phoneNumberBM.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.BM)
										.getDisplayNumber());
						phoneNumberP.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.P)
										.getDisplayNumber());
						phoneNumberPF.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.PF)
										.getDisplayNumber());
						phoneNumberPM.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.PM)
										.getDisplayNumber());
						phoneNumberVOIP.setText(address.getPhoneNumberWithCategory(PhoneNumber.Category.VOIP)
										.getDisplayNumber());

						if (callerInfo == null)
						{
							frame.setTitle(Engine.instance().getResourceService().getStringWithParams(
											"addressOf",
											address.getFirstName() + (StringTools.isNotTrimEmpty(firstName) ? " " : "")
															+ address.getLastName()
															+ (StringTools.isNotTrimEmpty(company) ? " " : "")
															+ address.getCompany()));
						}
					}
				});
			}
		}.start();
	}
}
