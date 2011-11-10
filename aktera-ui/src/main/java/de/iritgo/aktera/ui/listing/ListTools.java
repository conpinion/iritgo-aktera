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

package de.iritgo.aktera.ui.listing;


import de.iritgo.aktera.hibernate.StandardDao;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.el.ExpressionLanguageContext;
import de.iritgo.aktera.ui.form.CommandInfo;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.QueryDescriptor;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.dbutils.DbUtils;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * Utility class that helps to create lists of persistent objects.
 */
public class ListTools
{
	/**
	 * Create the listing response elements.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param handler
	 *            The listing handler.
	 * @param filler
	 *            The list filler.
	 * @throws ModelException.
	 */
	public static void createListing(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					ListingHandler handler, ListContext context, ListFiller filler)
		throws ModelException, PersistenceException
	{
		if (filler == null && listing.getQuerySample() != null)
		{
			createListingWithSample(request, response, listing, handler, context);
		}
		else if (listing.getQuery() != null)
		{
			createListingWithQuery(request, response, listing, handler, context);
		}
		else if (filler == null && listing.getPersistents() != null && listing.getCondition() != null)
		{
			createListingWithCondition(request, response, listing, handler, context);
		}
		else
		{
			createListingWithFiller(request, response, listing, filler, context);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param listing
	 * @param handler
	 * @param context
	 * @throws ModelException
	 */
	private static void createListingWithQuery(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					ListingHandler handler, ListContext context) throws ModelException, PersistenceException
	{
		StandardDao standardDao = (StandardDao) SpringTools.getBean(StandardDao.ID);
		QueryDescriptor query = listing.getQuery();
		Properties queryParams = ExpressionLanguageContext.evalExpressionLanguageValue(context, request, query
						.getParams());
		int count = 0;

		if (query.getDaoName() != null)
		{
			try
			{
				java.util.List list = (java.util.List) MethodUtils.invokeExactMethod(SpringTools.getBean(query
								.getDaoName()), query.getDaoMethodName(), queryParams);

				count = list.size();
			}
			catch (NoSuchMethodException x)
			{
				throw new ModelException(x);
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException(x);
			}
			catch (InvocationTargetException x)
			{
				throw new ModelException(x.getTargetException());
			}
		}
		else if (query.getCountName() != null)
		{
			count = (int) standardDao.countByNamedQuery(query.getCountName(), queryParams);
		}
		else if (query.getName() != null)
		{
			count = (int) standardDao.countByNamedFindQuery(query.getName(), queryParams);
		}
		else
		{
			if (query.getCountQuery() != null)
			{
				count = (int) standardDao.countByQuery(query.getCountQuery(), queryParams);
			}
			else
			{
				count = (int) standardDao.countByFindQuery(query.getQuery(), queryParams);
			}
		}

		int page = Math.max(listing.getPage(), 1);
		int maxPage = (Math.max(0, count - 1) / context.getResultsPerPage()) + 1;

		page = Math.min(page, maxPage);

		context.setPage(page);
		context.setFirstResult((page - 1) * context.getResultsPerPage());

		Output outList = createHeaderElements(request, response, listing, context);

		outList.setAttribute("pageCount", new Integer(maxPage));
		outList.setAttribute("page", String.valueOf(page));

		int firstResult = (page - 1) * context.getResultsPerPage();
		int maxResults = context.getResultsPerPage();
		java.util.List res = null;

		if (query.getDaoName() != null)
		{
			try
			{
				res = (java.util.List) MethodUtils.invokeExactMethod(SpringTools.getBean(query.getDaoName()), query
								.getDaoMethodName(), queryParams);
			}
			catch (NoSuchMethodException x)
			{
				throw new ModelException(x);
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException(x);
			}
			catch (InvocationTargetException x)
			{
				throw new ModelException(x.getTargetException());
			}
		}
		else if (query.getName() != null)
		{
			res = standardDao.findByNamedQuery(query.getName(), queryParams, firstResult, maxResults, listing
							.getSortColumnName(), listing.getSortOrder());
		}
		else
		{
			res = standardDao.findByQuery(query.getQuery(), queryParams, firstResult, maxResults, listing
							.getSortColumnName(), listing.getSortOrder());
		}

		int rowNum = 1;

		for (Object row : res)
		{
			context.setIt(row);

			Output outItem = response.createOutput("item");

			outList.add(outItem);

			String idExpression = listing.getIdColumn();

			if (idExpression == null)
			{
				idExpression = "#{it.id}";
			}

			String id = ExpressionLanguageContext.evalExpressionLanguageValue(context, request, idExpression)
							.toString();

			try
			{
				outItem.setAttribute("id", id);
			}
			catch (Exception x)
			{
				throw new ModelException("Unable to retrieve id property in listing '" + listing.getId() + "' ("
								+ x.getMessage() + ")");
			}

			outItem.setAttribute("odd", new Boolean(rowNum % 2 == 1));
			outItem.setAttribute("even", new Boolean(rowNum % 2 == 0));
			++rowNum;

			int colNum = 0;

			for (ColumnDescriptor column : listing.getColumns())
			{
				Output outColumn = response.createOutput(String.valueOf(colNum++));
				outItem.add(outColumn);

				if ("custom".equals(column.getViewer()))
				{
					outColumn.setAttribute("viewer", "text");
					if (handler != null)
					{
						try
						{
							CellData cellData = handler.handleResult(request, response, listing, new BeanRowData(
											listing, row), column);
							outColumn.setContent(cellData.getValue());
							outColumn.setAttribute("viewer", cellData.getViewer().toString());
							outColumn.setAttribute("bundle", cellData.getBundle());
						}
						catch (Exception x)
						{
							outColumn.setAttribute("viewer", "error");
							outColumn.setContent(x.getMessage());
						}
					}
					else
					{
						outColumn.setContent("");
					}
				}
				else
				{
					String valueExpression = column.getValue();
					if (valueExpression == null)
					{
						valueExpression = "#{it." + column.getName() + "}";
					}
					Object value = ExpressionLanguageContext.evalExpressionLanguageValue(context, request,
									valueExpression);

					if ("combo".equals(column.getViewer()))
					{
						outColumn.setAttribute("viewer", column.getViewer());
						outColumn.setAttribute("bundle", column.getBundle());
						outColumn.setContent(! StringTools.isTrimEmpty(value) ? "opt" + value : "-");
					}
					else if ("message".equals(column.getViewer()))
					{
						outColumn.setAttribute("viewer", column.getViewer());
						outColumn.setAttribute("bundle", column.getBundle());

						String[] parts = value.toString().split("\\|");
						StringBuffer sb = new StringBuffer(parts[0]);

						for (int j = 1; j < parts.length; ++j)
						{
							if (parts[j].startsWith("#{") && parts[j].endsWith("}"))
							{
								sb.append("|"
												+ ExpressionLanguageContext.evalExpressionLanguageValue(context,
																request, parts[j]));
							}
							else
							{
								sb.append("|" + parts[j]);
							}
						}

						outColumn.setContent(sb.toString());
					}
					else if (column.getViewer().startsWith("icon:"))
					{
						String viewer = column.getViewer();
						int slashPos = viewer.indexOf('|');
						String iconTrue = viewer.substring(5, slashPos > 0 ? slashPos : viewer.length());
						String iconFalse = slashPos > 0 ? viewer.substring(slashPos + 1, viewer.length()) : "blank";
						outColumn.setAttribute("viewer", "icon");
						outColumn.setContent(NumberTools.toBool(value, false) ? iconTrue : iconFalse);
					}
					else if (column.getViewer().startsWith("handler:"))
					{
						try
						{
							CellData cellData = (CellData) MethodUtils.invokeMethod(handler, column.getViewer()
											.substring(8), value);
							outColumn.setContent(cellData.getValue());
							outColumn.setAttribute("viewer", cellData.getViewer() != null ? cellData.getViewer()
											.toString() : "text");
							outColumn.setAttribute("bundle", cellData.getBundle() != null ? cellData.getBundle()
											: listing.getBundle());
						}
						catch (InvocationTargetException x)
						{
							outColumn.setContent(x.getTargetException().toString());
						}
						catch (Exception x)
						{
							outColumn.setContent(x.toString());
						}
					}
					else if (column.getViewer().startsWith("js:"))
					{
						String[] params = column.getViewer().split(":");

						outColumn.setAttribute("viewer", "js");
						outColumn.setContent(params[1]);
						outColumn.setAttribute("name", column.getName());
						outColumn.setAttribute("id", id);
					}
					else if ("province".equals(column.getViewer()))
					{
						//					outColumn.setAttribute ("viewer", column.getViewer ());
						//					Object country = null;
						//					try
						//					{
						//						country = set.getObject (column.getAs () + "country");
						//					}
						//					catch (Exception ignored)
						//					{
						//					}
						//					Object value = set.getObject (column.getAs ());
						//					if (! StringTools.isTrimEmpty (value))
						//					{
						//						outColumn.setContent ((! StringTools.isTrimEmpty (country) ? country + "." : "") +
						//										StringTools.trim (value));
						//					}
						//					else
						//					{
						//						outColumn.setContent ("");
						//					}
						outColumn.setContent("");
					}
					else
					{
						outColumn.setAttribute("viewer", column.getViewer());
						outColumn.setContent(! StringTools.isTrimEmpty(value) ? value : "");
					}
				}

				if (! column.isVisible())
				{
					outColumn.setAttribute("hide", "Y");
				}
			}

			CommandInfo viewCommand = listing.getCommand(ListingDescriptor.COMMAND_VIEW);

			if (viewCommand != null)
			{
				Command cmd = viewCommand.createCommand(request, response, context);

				cmd.setName("command");
				cmd.setParameter(listing.getKeyName(), id);
				cmd.setParameter("link", "Y");
				outItem.setAttribute("command", cmd);
			}
		}

		while (rowNum <= context.getResultsPerPage())
		{
			Output outItem = response.createOutput("item");

			outList.add(outItem);
			outItem.setAttribute("empty", Boolean.TRUE);
			++rowNum;
		}

		createListingControls(request, response, listing, outList, 1, maxPage, page, context);
	}

	/**
	 * Create the listing response elements.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param handler
	 *            The listing handler.
	 * @param context
	 *            TODO
	 * @throws ModelException.
	 */
	private static void createListingWithSample(ModelRequest request, ModelResponse response,
					ListingDescriptor listing, ListingHandler handler, ListContext context)
		throws ModelException, PersistenceException
	{
		Output outList = createHeaderElements(request, response, listing, context);

		int page = Math.max(listing.getPage(), 1);

		int maxPage = 1;

		if (! context.isDescribe())
		{
			int count = listing.getQuerySample().count();

			outList.setAttribute("count", new Integer(count));

			maxPage = (Math.max(0, count - 1) / context.getResultsPerPage()) + 1;
			page = Math.min(page, maxPage);

			context.setPage(page);
			context.setFirstResult((page - 1) * context.getResultsPerPage());

			outList.setAttribute("pageCount", new Integer(maxPage));
			outList.setAttribute("page", String.valueOf(page));

			listing.getQuerySample().setMaxRecords(context.getResultsPerPage());
			listing.getQuerySample().setOffsetRecord((page - 1) * context.getResultsPerPage());

			int num = 1;

			String sortColumn = listing.getSortColumnName();

			if (listing.getSortOrder() == SortOrder.DESCENDING)
			{
				sortColumn += " DESC";
			}

			Iterator result = listing.getQuerySample().query(sortColumn).iterator();

			while (result.hasNext())
			{
				Persistent persistent = (Persistent) result.next();

				Output outItem = response.createOutput("item");

				outList.add(outItem);

				outItem.setAttribute("id", persistent.getFieldString(listing.getIdField()));
				outItem.setAttribute("odd", new Boolean(num % 2 == 1));
				outItem.setAttribute("even", new Boolean(num % 2 == 0));

				handleResult(request, response, listing, outItem, persistent, context);

				++num;
			}

			while (num < context.getResultsPerPage())
			{
				Output outItem = response.createOutput("item");

				outList.add(outItem);
				outItem.setAttribute("empty", Boolean.TRUE);
				++num;
			}
		}

		createListingControls(request, response, listing, outList, 1, maxPage, page, context);
	}

	/**
	 * Create the listing response elements.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param handler
	 *            The listing handler.
	 * @param context
	 *            TODO
	 * @throws ModelException.
	 */
	private static void createListingWithCondition(ModelRequest request, ModelResponse response,
					ListingDescriptor listing, ListingHandler handler, ListContext context)
		throws ModelException, PersistenceException
	{
		Output outList = createHeaderElements(request, response, listing, context);

		int count = 0;
		int page = Math.max(listing.getPage(), 1);
		int maxPage = 1;

		if (! context.isDescribe())
		{
			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet set = null;

			try
			{
				DataSourceComponent dataSource = (DataSourceComponent) request.getService(DataSourceComponent.ROLE,
								"keel-dbpool");

				connection = dataSource.getConnection();

				stmt = createPreparedStatement(listing, connection, createSqlSelectFields(listing) + " "
								+ createSqlFrom(listing) + " " + createSqlWhere(request, listing));
				set = stmt.executeQuery();
				set.last();
				count = set.getRow();

				maxPage = (Math.max(0, count - 1) / context.getResultsPerPage()) + 1;
				page = Math.min(page, maxPage);

				context.setPage(page);
				context.setFirstResult((page - 1) * context.getResultsPerPage());

				outList.setAttribute("pageCount", new Integer(maxPage));
				outList.setAttribute("page", String.valueOf(page));

				set.beforeFirst();

				int num = 1;

				int offsetRecord = (page - 1) * context.getResultsPerPage();
				int recordCount = 0;
				int retrieveCount = 0;

				while (set.next())
				{
					recordCount++;
					retrieveCount++;

					if (retrieveCount < offsetRecord && offsetRecord > 0)
					{
						continue;
					}
					else if (retrieveCount == offsetRecord && offsetRecord > 0)
					{
						recordCount = 0;

						continue;
					}

					Output outItem = response.createOutput("item");

					outList.add(outItem);

					StringBuffer ids = null;

					if (listing.getNumIdColumns() == 1)
					{
						ids = new StringBuffer(set.getString("id") != null ? set.getString("id") : "0");
					}
					else
					{
						ids = new StringBuffer();

						int col = 1;

						for (Iterator i = listing.getIdColumns().iterator(); i.hasNext();)
						{
							ListingDescriptor.IdColumnInfo idInfo = (ListingDescriptor.IdColumnInfo) i.next();

							if (col > 1)
							{
								ids.append("|");
							}

							ids.append(idInfo.field + ":"
											+ (set.getString("id" + col) != null ? set.getString("id" + col) : "0"));
							++col;
						}
					}

					outItem.setAttribute("id", ids.toString());
					outItem.setAttribute("odd", new Boolean(num % 2 == 1));
					outItem.setAttribute("even", new Boolean(num % 2 == 0));

					handleResult(request, response, listing, outItem, set, handler, context);

					++num;

					if (recordCount == context.getResultsPerPage())
					{
						break;
					}
				}

				while (num <= context.getResultsPerPage())
				{
					Output outItem = response.createOutput("item");

					outList.add(outItem);
					outItem.setAttribute("empty", Boolean.TRUE);
					++num;
				}
			}
			catch (SQLException x)
			{
				System.out.println("[ListTools] SQLError: " + x);
				throw new ModelException("[ListTools] SQLError " + x);
			}
			finally
			{
				DbUtils.closeQuietly(set);
				DbUtils.closeQuietly(stmt);
				DbUtils.closeQuietly(connection);
			}

			outList.setAttribute("count", new Integer(count));
		}

		createListingControls(request, response, listing, outList, 1, maxPage, page, context);
	}

	/**
	 * @param request
	 * @param response
	 * @param listing
	 * @param filler
	 * @param context
	 *            TODO
	 * @throws ModelException
	 */
	private static void createListingWithFiller(ModelRequest request, ModelResponse response,
					ListingDescriptor listing, ListFiller filler, ListContext context) throws ModelException
	{
		Output outList = createHeaderElements(request, response, listing, context);

		int page = Math.max(listing.getPage(), 1);
		int maxPage = 1;

		if (! context.isDescribe())
		{
			outList.setAttribute("count", new Integer(filler.getRowCount()));

			long totalCount = filler.getTotalRowCount();

			if (totalCount < 0)
			{
				totalCount = filler.getRowCount();
			}

			page = Math.max(request.getParameterAsInt(context.getListName() + "Page", 1), 1);

			if (request.hasParameter(context.getListName() + "Page"))
			{
				page = Math.max(request.getParameterAsInt(context.getListName() + "Page", 1), 1);
			}

			maxPage = (int) ((Math.max(0, totalCount - 1) / context.getResultsPerPage()) + 1);
			page = Math.min(page, maxPage);

			context.setPage(page);
			context.setFirstResult((page - 1) * context.getResultsPerPage());

			outList.setAttribute("pageCount", new Integer(maxPage));
			outList.setAttribute("page", String.valueOf(page));

			int num = 1;
			int recordCount = 0;
			int retrieveCount = 0;
			int offsetRecord = (page - 1) * context.getResultsPerPage();

			while (filler.next())
			{
				recordCount++;

				if (filler.getTotalRowCount() < 0)
				{
					retrieveCount++;

					if (retrieveCount < offsetRecord && offsetRecord > 0)
					{
						continue;
					}
					else if (retrieveCount == offsetRecord && offsetRecord > 0)
					{
						recordCount = 0;

						continue;
					}
				}

				Output outItem = response.createOutput("item");

				outList.add(outItem);
				outItem.setAttribute("id", filler.getId());
				outItem.setAttribute("odd", new Boolean(num % 2 == 1));
				outItem.setAttribute("even", new Boolean(num % 2 == 0));

				int colNum = 0;

				for (Iterator j = listing.columnIterator(); j.hasNext();)
				{
					ColumnDescriptor column = (ColumnDescriptor) j.next();

					Output outColumn = response.createOutput(String.valueOf(colNum++));
					String value = StringTools.trim(filler.getValue(column.getField()));

					if (column.getViewer().startsWith("js:"))
					{
						outColumn.setContent(value);

						String[] params = column.getViewer().split(":");

						outColumn.setAttribute("viewer", "js");
						outColumn.setContent(params[1]);
						outColumn.setAttribute("name", column.getName());
						outColumn.setAttribute("id", filler.getId());
					}
					else if (column.getViewer().equals("message"))
					{
						outColumn.setAttribute("viewer", "message");

						int bundlePos = value.indexOf(":");

						if (bundlePos >= 0)
						{
							outColumn.setAttribute("bundle", value.substring(0, bundlePos));
						}
						else
						{
							outColumn.setAttribute("bundle", column.getBundle());
						}

						outColumn.setContent(value.substring(bundlePos + 1));
					}
					else
					{
						outColumn.setContent(value);
						outColumn.setAttribute("viewer", column.getViewer());
					}

					if (! column.isVisible())
					{
						outColumn.setAttribute("hide", "Y");
					}

					outItem.add(outColumn);
				}

				CommandInfo viewCommand = listing.getCommand(ListingDescriptor.COMMAND_VIEW);

				if (viewCommand != null)
				{
					Command cmd = viewCommand.createCommand(request, response, context);

					cmd.setName("command");
					cmd.setParameter("id", filler.getId());
					cmd.setParameter("link", "Y");
					outItem.setAttribute("command", cmd);
				}

				++num;

				if (recordCount == context.getResultsPerPage())
				{
					break;
				}
			}

			while (num <= context.getResultsPerPage())
			{
				Output outItem = response.createOutput("item");

				outList.add(outItem);
				outItem.setAttribute("empty", Boolean.TRUE);
				++num;
			}
		}

		createListingControls(request, response, listing, outList, 1, maxPage, page, context);
	}

	/**
	 * Handle one item of the query result.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param outItem
	 *            The generated output for the result item.
	 * @param persistent
	 *            The resulting persitent.
	 */
	private static void handleResult(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					Output outItem, Persistent persistent, ListContext context)
		throws PersistenceException, ModelException
	{
		int colNum = 0;

		for (Iterator i = listing.columnIterator(); i.hasNext();)
		{
			ColumnDescriptor column = (ColumnDescriptor) i.next();

			String fieldName = column.getName().substring(column.getName().lastIndexOf('.') + 1);

			Output outColumn = response.createOutput(String.valueOf(colNum++));

			outColumn.setContent(persistent.getField(fieldName));
			outColumn.setAttribute("viewer", column.getViewer());

			if (! column.isVisible())
			{
				outColumn.setAttribute("hide", "Y");
			}

			outItem.add(outColumn);
		}

		CommandInfo viewCommand = listing.getCommand(ListingDescriptor.COMMAND_VIEW);

		if (viewCommand != null)
		{
			Command cmd = viewCommand.createCommand(request, response, context);

			cmd.setName("command");
			cmd.setParameter(listing.getKeyName(), persistent.getFieldString(listing.getIdColumn()));
			cmd.setParameter("link", "Y");
			outItem.setAttribute("command", cmd);
		}
	}

	/**
	 * Generate the SQL SELECT fields clause for a given listing.
	 *
	 * @param listing
	 *            The listing for which to generate the statement
	 * @return The SQL SELECT fields clause (SELECT a,b,c...)
	 */
	private static String createSqlSelectFields(ListingDescriptor listing)
		throws ModelException, PersistenceException, SQLException
	{
		PersistentDescriptor persistents = listing.getPersistents();
		StringBuffer sql = new StringBuffer("SELECT ");
		PersistentMetaData idPersistentMeta = persistents.getMetaData(listing.getIdPersistent());

		if (idPersistentMeta == null)
		{
			throw new ModelException("ListTools: Unknown persistent specified for id column '" + listing.getIdColumn()
							+ "'");
		}

		if (listing.getNumIdColumns() == 1)
		{
			sql.append(listing.getIdPersistent() + "." + idPersistentMeta.getDBFieldName(listing.getIdField())
							+ " AS id");
		}
		else
		{
			int col = 1;

			for (Iterator i = listing.getIdColumns().iterator(); i.hasNext();)
			{
				ListingDescriptor.IdColumnInfo idInfo = (ListingDescriptor.IdColumnInfo) i.next();

				if (col > 1)
				{
					sql.append(", ");
				}

				sql
								.append(idInfo.persistent + "." + idPersistentMeta.getDBFieldName(idInfo.field)
												+ " AS id" + (col++));
			}
		}

		for (Iterator i = listing.columnIterator(); i.hasNext();)
		{
			ColumnDescriptor column = (ColumnDescriptor) i.next();

			if ("custom".equals(column.getViewer()) || column.getViewer().startsWith("js:"))
			{
				continue;
			}

			sql.append(", ");

			PersistentMetaData columnPersistentMeta = persistents.getMetaData(column.getPersistent());

			if (columnPersistentMeta == null)
			{
				throw new ModelException("ListTools: Unknown persistent specified for column '" + column.getName()
								+ "'");
			}

			sql.append(column.getPersistent() + "." + columnPersistentMeta.getDBFieldName(column.getField()) + " AS "
							+ column.getAs());
		}

		return sql.toString();
	}

	/**
	 * Generate the SQL FROM clause for a given listing.
	 *
	 * @param listing
	 *            The listing for which to generate the statement
	 * @return The SQL FROM clause
	 */
	private static String createSqlFrom(ListingDescriptor listing)
		throws ModelException, PersistenceException, SQLException
	{
		PersistentDescriptor persistents = listing.getPersistents();
		StringBuffer sql = new StringBuffer("");
		Iterator iKey = persistents.keyIterator();

		if (! iKey.hasNext())
		{
			throw new ModelException("No persistents defined for listing " + listing.getHeader());
		}

		String key = (String) iKey.next();
		PersistentMetaData persistentMeta = persistents.getMetaData(key);

		sql.append(" FROM " + persistentMeta.getTableName() + " AS " + key);

		for (; iKey.hasNext();)
		{
			key = (String) iKey.next();
			persistentMeta = persistents.getMetaData(key);
			sql.append(" LEFT JOIN " + persistentMeta.getTableName() + " AS " + key + " ON ");

			PersistentDescriptor.JoinInfo join = persistents.getJoin(key);

			if (join == null)
			{
				throw new ModelException("ListTools: No join info defined for persistent '" + key + "'");
			}

			PersistentMetaData persistentMetaJoin = persistents.getMetaData(join.getPersistent());

			sql.append(join.getPersistent() + "." + persistentMetaJoin.getDBFieldName(join.getKey()) + " = " + key
							+ "." + persistentMeta.getDBFieldName(join.getMyKey()));

			if (join.getCondition() != null)
			{
				sql.append(" AND " + join.getCondition());
			}
		}

		return sql.toString();
	}

	/**
	 * Generate the SQL WHERE clause for a given listing.
	 *
	 * @param request
	 *            The model request
	 * @param listing
	 *            The listing for which to generate the statement
	 * @return The SQL WHERE clause
	 */
	private static String createSqlWhere(ModelRequest request, ListingDescriptor listing) throws PersistenceException
	{
		PersistentDescriptor persistents = listing.getPersistents();
		PersistentMetaData idPersistentMeta = persistents.getMetaData(listing.getIdPersistent());

		StringBuffer condition = new StringBuffer();

		if (! StringTools.isTrimEmpty(listing.getCondition()))
		{
			condition.append(" WHERE " + listing.getCondition());
		}

		if (listing.getSortColumns().size() > 0)
		{
			condition.append(" ORDER BY ");
		}

		for (Iterator i = listing.sortColumnIterator(); i.hasNext();)
		{
			ColumnDescriptor column = (ColumnDescriptor) i.next();

			condition.append(column.getName() + (column.getSort() == SortOrder.DESCENDING ? " DESC" : " ASC"));

			if (i.hasNext())
			{
				condition.append(", ");
			}
		}

		StringBuffer sqlCondition = new StringBuffer();

		for (StringTokenizer st = new StringTokenizer(condition.toString()); st.hasMoreTokens();)
		{
			String token = st.nextToken();
			int dotIndex = token.indexOf('.');

			if (dotIndex != - 1)
			{
				String persistent = token.substring(0, dotIndex);
				String field = token.substring(dotIndex + 1);
				PersistentMetaData meta = persistents.getMetaData(persistent);

				if (meta != null)
				{
					sqlCondition.append(" " + persistent + "." + meta.getDBFieldName(field) + " ");
				}
			}
			else if (token.startsWith("#") || (token.startsWith("'#") && token.endsWith("'")))
			{
				String paramName = null;
				String quote = token.startsWith("'") ? "'" : "";

				if (token.startsWith("'"))
				{
					paramName = token.substring(2, token.length() - 1);
				}
				else
				{
					paramName = token.substring(1);
				}

				String param = request.getParameterAsString(paramName);

				if (paramName.equals("id") && request.getParameter("prevId") != null)
				{
					param = request.getParameterAsString("prevId");
				}

				if (StringTools.isTrimEmpty(param))
				{
					param = "0";
				}

				sqlCondition.append(" " + quote + param + quote + " ");
			}
			else if (token.startsWith("$") || token.startsWith("~") || (token.startsWith("'$") && token.endsWith("'"))
							|| (token.startsWith("'~") && token.endsWith("'")))
			{
				String var = null;
				String quote = token.startsWith("'") ? "'" : "";
				boolean like = token.startsWith("~") || token.startsWith("'~");

				if (token.startsWith("'"))
				{
					var = token.substring(2, token.length() - 1).toLowerCase();
				}
				else
				{
					var = token.substring(1).toLowerCase();
				}

				String value = null;

				if ("search".compareToIgnoreCase(var) == 0)
				{
					value = request.getParameterAsString(listing.getId(request) + "Search");
				}
				else if ("searchcategory".compareToIgnoreCase(var) == 0)
				{
					value = listing.getCategory();
				}
				else if ("userid".compareToIgnoreCase(var) == 0)
				{
					value = UserTools.getCurrentUserId(request).toString();
				}

				value = StringTools.trim(value);
				sqlCondition.append(" " + quote);

				if (like)
				{
					sqlCondition.append("%" + (StringTools.isEmpty(value) ? "" : value) + "%");
				}
				else
				{
					sqlCondition.append(StringTools.isEmpty(value) ? "0" : value);
				}

				sqlCondition.append(quote + " ");
			}
			else if ("like".compareToIgnoreCase(token) == 0)
			{
				sqlCondition.append(idPersistentMeta.getDatabaseType().getLikeStatement());
			}
			else
			{
				sqlCondition.append(" " + token + " ");
			}
		}

		return sqlCondition.toString();
	}

	/**
	 * Create a prepared statement from the given SQL query.
	 *
	 * @param listing
	 *            The listing for which to generate the statement
	 * @param connection
	 *            The JDBC connection
	 * @param sql
	 *            The SQL statement
	 * @return The prepared statement
	 * @throws SQLException
	 *             In case of an SQL error
	 */
	private static PreparedStatement createPreparedStatement(ListingDescriptor listing, Connection connection,
					String sql) throws PersistenceException, SQLException
	{
		PreparedStatement stmt = connection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);

		if (listing.getCondition() != null && listing.getConditionParams() != null)
		{
			for (int i = 0; i < listing.getConditionParams().length; ++i)
			{
				stmt.setObject(i + 1, listing.getConditionParams()[i]);
			}
		}

		return stmt;
	}

	/**
	 * Handle one item of the query result.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param outItem
	 *            The generated output for the result item.
	 * @param set
	 *            The result set.
	 * @param handler
	 *            The listing handler.
	 */
	private static void handleResult(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					Output outItem, ResultSet set, ListingHandler handler, ListContext context)
		throws PersistenceException, ModelException, SQLException
	{
		String ids = "";

		if (listing.getNumIdColumns() == 1)
		{
			ids = set.getString("id");
		}
		else
		{
			StringBuffer idsBuf = new StringBuffer();

			int col = 1;

			for (Iterator i = listing.getIdColumns().iterator(); i.hasNext();)
			{
				ListingDescriptor.IdColumnInfo idInfo = (ListingDescriptor.IdColumnInfo) i.next();

				if (col > 1)
				{
					idsBuf.append("|");
				}

				idsBuf.append(idInfo.field + ":"
								+ (set.getString("id" + col) != null ? set.getString("id" + col) : "0"));
				++col;
			}

			ids = idsBuf.toString();
		}

		int colNum = 0;

		for (Iterator i = listing.columnIterator(); i.hasNext();)
		{
			ColumnDescriptor column = (ColumnDescriptor) i.next();

			Output outColumn = response.createOutput(String.valueOf(colNum++));

			if ("custom".equals(column.getViewer()))
			{
				outColumn.setAttribute("viewer", "text");

				if (handler != null)
				{
					CellData cellData = handler.handleResult(request, response, listing, new SqlRowData(listing, set),
									column);

					outColumn.setContent(cellData.getValue());
					outColumn.setAttribute("viewer", cellData.getViewer().toString());
					outColumn.setAttribute("bundle", cellData.getBundle());
				}
				else
				{
					outColumn.setContent("");
				}
			}
			else if ("combo".equals(column.getViewer()))
			{
				Object value = set.getObject(column.getAs());

				outColumn.setAttribute("viewer", column.getViewer());
				outColumn.setAttribute("bundle", column.getBundle());
				outColumn.setContent(! StringTools.isTrimEmpty(value) ? "opt" + value : "-");
			}
			else if ("message".equals(column.getViewer()))
			{
				outColumn.setAttribute("viewer", column.getViewer());

				Object value = set.getObject(column.getAs());

				outColumn.setAttribute("bundle", column.getBundle());

				String[] parts = value.toString().split("\\|");
				StringBuffer sb = new StringBuffer(parts[0]);

				for (int j = 1; j < parts.length; ++j)
				{
					if (parts[j].startsWith("?"))
					{
						sb.append("|" + set.getString(parts[j].substring(1).replace('.', '_')));
					}
					else
					{
						sb.append("|" + parts[j]);
					}
				}

				outColumn.setContent(sb.toString());
			}
			else if (column.getViewer().startsWith("icon:"))
			{
				Object value = set.getObject(column.getAs());
				String viewer = column.getViewer();
				int slashPos = viewer.indexOf('|');
				String iconTrue = viewer.substring(5, slashPos > 0 ? slashPos : viewer.length());
				String iconFalse = slashPos > 0 ? viewer.substring(slashPos + 1, viewer.length()) : "blank";
				outColumn.setAttribute("viewer", "icon");
				outColumn.setContent(NumberTools.toBool(value, false) ? iconTrue : iconFalse);
			}
			else if (column.getViewer().startsWith("handler:"))
			{
				Object value = set.getObject(column.getAs());

				outColumn.setAttribute("viewer", "text");

				try
				{
					Class cellHandler = Class.forName(column.getViewer().substring(8));
					String cellRes = cellHandler.getMethod("format", new Class[]
					{
						Object.class
					}).invoke(null, value).toString();

					outColumn.setContent(cellRes);
				}
				catch (InvocationTargetException x)
				{
					outColumn.setContent(x.getTargetException().toString());
				}
				catch (Exception x)
				{
					outColumn.setContent(x.toString());
				}
			}
			else if (column.getViewer().startsWith("js:"))
			{
				String[] params = column.getViewer().split(":");

				outColumn.setAttribute("viewer", "js");
				outColumn.setContent(params[1]);
				outColumn.setAttribute("name", column.getName());
				outColumn.setAttribute("id", ids);
			}
			else if ("province".equals(column.getViewer()))
			{
				outColumn.setAttribute("viewer", column.getViewer());

				Object country = null;

				try
				{
					country = set.getObject(column.getAs() + "country");
				}
				catch (Exception ignored)
				{
				}

				Object value = set.getObject(column.getAs());

				if (! StringTools.isTrimEmpty(value))
				{
					outColumn.setContent((! StringTools.isTrimEmpty(country) ? country + "." : "")
									+ StringTools.trim(value));
				}
				else
				{
					outColumn.setContent("");
				}
			}
			else
			{
				outColumn.setAttribute("viewer", column.getViewer());

				Object value = set.getObject(column.getAs());

				outColumn.setContent(! StringTools.isTrimEmpty(value) ? value : "");
			}

			if (! column.isVisible())
			{
				outColumn.setAttribute("hide", "Y");
			}

			outItem.add(outColumn);
		}

		CommandInfo viewCommand = listing.getCommand(ListingDescriptor.COMMAND_VIEW);

		if (viewCommand != null)
		{
			Command cmd = viewCommand.createCommand(request, response, context);

			cmd.setName("command");
			cmd.setParameter(listing.getKeyName(), ids);
			cmd.setParameter("link", "Y");
			outItem.setAttribute("command", cmd);
		}
	}

	/**
	 * Create the page navigation controls.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param outList
	 *            The list output element.
	 * @param name
	 *            The command name.
	 * @throws ModelException.
	 */
	private static void createListingControls(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					Output outList, int minPage, int maxPage, int currentPage, ListContext context)
		throws ModelException
	{
		outList.setAttribute("keyName", listing.getKeyName());

		outList.setAttribute("headerTitle", listing.getHeader());

		outList.setAttribute("bundle", listing.getBundle());

		if (listing.getTitle() != null)
		{
			outList.setAttribute("title", listing.getTitle());
			outList.setAttribute("titleBundle", listing.getTitleBundle() != null ? listing.getTitleBundle() : listing
							.getBundle());
		}

		if (listing.getIcon() != null)
		{
			outList.setAttribute("icon", listing.getIcon());
		}

		if (listing.isEmbedded())
		{
			outList.setAttribute("embedded", "Y");
		}

		outList.setAttribute("commandStyle", listing.getCommandStyle());

		CommandInfo viewCommand = listing.getCommand(ListingDescriptor.COMMAND_VIEW);

		if (viewCommand != null && viewCommand.checkPermission(request))
		{
			Command cmd = viewCommand.createCommand(request, response, context);

			cmd.setName(outList.getName() + "CmdEdit");
			cmd.setLabel(viewCommand.getLabel() != null ? viewCommand.getLabel() : "edit");
			outList.setAttribute("cmdEdit", cmd);
		}

		CommandInfo cmdNew = listing.getCommand(ListingDescriptor.COMMAND_NEW);

		if (cmdNew != null && cmdNew.isVisible() && cmdNew.checkPermission(request))
		{
			Command cmd = cmdNew.createCommand(request, response, context);

			cmd.setName(outList.getName() + "CmdNew");
			outList.setAttribute("cmdNew", cmd);
		}

		String outListName = outList.getName();

		if (currentPage > 1)
		{
			Command cmdPageStart = createPageCommand(request, response, listing, "cmdPageStart", new String[]
			{
				"~listSort"
			});

			cmdPageStart.setParameter(outListName + "Page", String.valueOf(minPage));
			cmdPageStart.setLabel("$start");
			outList.setAttribute("cmdPageStart", cmdPageStart);

			Command cmdPageBack = createPageCommand(request, response, listing, "cmdPageBack", new String[]
			{
				"~listSort"
			});

			cmdPageBack.setParameter(outListName + "Page", String.valueOf(currentPage - 1));
			cmdPageBack.setLabel("$back");
			outList.setAttribute("cmdPageBack", cmdPageBack);

			Output outPrevPages = response.createOutput("prevPages");

			outList.setAttribute("prevPages", outPrevPages);

			int firstPrevPage = Math.max(minPage, currentPage - context.getNumPrevPages());

			for (int i = currentPage - 1; i >= firstPrevPage; --i)
			{
				Command cmdPage = createPageCommand(request, response, listing, "cmdPage", new String[]
				{
					"~listSort"
				});

				cmdPage.setParameter(outListName + "Page", String.valueOf(currentPage - i - 1 + firstPrevPage));
				cmdPage.setLabel(String.valueOf(currentPage - i - 1 + firstPrevPage));
				outPrevPages.add(cmdPage);
			}
		}

		if (currentPage < maxPage)
		{
			Command cmdPageEnd = createPageCommand(request, response, listing, "cmdPageEnd", new String[]
			{
				"~listSort"
			});

			cmdPageEnd.setParameter(outListName + "Page", String.valueOf(maxPage));
			cmdPageEnd.setLabel("$end");
			outList.setAttribute("cmdPageEnd", cmdPageEnd);

			Command cmdPageNext = createPageCommand(request, response, listing, "cmdPageNext", new String[]
			{
				"~listSort"
			});

			cmdPageNext.setParameter(outListName + "Page", String.valueOf(currentPage + 1));
			cmdPageNext.setLabel("$next");
			outList.setAttribute("cmdPageNext", cmdPageNext);

			Output outNextPages = response.createOutput("nextPages");

			outList.setAttribute("nextPages", outNextPages);

			int lastNextPage = Math.min(maxPage, currentPage + context.getNumNextPages());

			for (int i = currentPage + 1; i <= lastNextPage; ++i)
			{
				Command cmdPage = createPageCommand(request, response, listing, "page", new String[]
				{
					"~listSort"
				});

				cmdPage.setParameter(outListName + "Page", String.valueOf(i));
				cmdPage.setLabel(String.valueOf(i));
				outNextPages.add(cmdPage);
			}
		}

		if (listing.getListCommands() != null)
		{
			Output outCommands = response.createOutput("commands");

			outList.setAttribute("commands", outCommands);

			for (Iterator i = listing.getListCommands().iterator(); i.hasNext();)
			{
				CommandInfo descriptor = (CommandInfo) i.next();
				Command command = descriptor.createCommand(request, response, context);

				outCommands.add(command);
			}
		}

		if (listing.getItemCommands() != null)
		{
			Output outItemCommands = response.createOutput("itemCommands");

			CommandInfo viewCmd = listing.getCommand(ListingDescriptor.COMMAND_VIEW);

			if (viewCmd != null && viewCmd.checkPermission(request))
			{
				Command cmd = viewCmd.createCommand(request, response, context);

				outItemCommands.add(cmd);
			}

			outItemCommands.setAttribute("label", listing.getItemCommands().getLabel());
			outList.setAttribute("itemCommands", outItemCommands);

			for (Iterator i = listing.getItemCommands().iterator(); i.hasNext();)
			{
				CommandInfo descriptor = (CommandInfo) i.next();

				if (viewCmd == null || ! descriptor.getName().equals(viewCmd.getName()))
				{
					Command command = descriptor.createCommand(request, response, context);

					outItemCommands.add(command);
				}
			}

			Command cmdExecute;

			if (listing.getCommand(ListingDescriptor.COMMAND_EXECUTE) == null)
			{
				if (listing.isNg())
				{
					cmdExecute = response.createCommand(null);
					cmdExecute.setBean("de.iritgo.aktera.ui.ExecuteListItemCommand");
				}
				else
				{
					cmdExecute = response.createCommand("aktera.tools.execute-listitem-command");
				}

				cmdExecute.setName(outListName + "CmdExecute");
				cmdExecute.setLabel("execute");
			}
			else
			{
				cmdExecute = listing.getCommand(ListingDescriptor.COMMAND_EXECUTE).createCommand(request, response,
								"_lep", context);
			}

			outList.setAttribute("cmdExecute", cmdExecute);
			cmdExecute.setParameter("_lm", listing.getListModel() != null ? listing.getListModel() : ModelTools
							.getPreviousModel(request));

			for (Iterator i = request.getParameters().keySet().iterator(); i.hasNext();)
			{
				String key = (String) i.next();

				if ("model".equals(key) || "orig-model".equals(key) || key.startsWith("_lp"))
				{
					continue;
				}

				cmdExecute.setParameter("_lp" + key, request.getParameter(key));
			}

			for (Iterator i = listing.getItemCommands().iterator(); i.hasNext();)
			{
				CommandInfo descriptor = (CommandInfo) i.next();

				descriptor.setParameters(request, cmdExecute, "_lp", context);
			}
		}

		CommandInfo cmdSearch = listing.getCommand(ListingDescriptor.COMMAND_SEARCH);

		if (cmdSearch != null)
		{
			String searchInputName = outListName + "Search";

			Input input = response.createInput(searchInputName);

			input.setLabel("search");
			input.setDefaultValue(request.getParameterAsString(searchInputName));
			response.add(input);

			if (listing.getCategories() != null)
			{
				input = response.createInput(searchInputName + "Category");
				input.setDefaultValue(listing.getCategory());
				input.setValidValues(listing.getCategories());
				response.add(input);
			}

			Command cmd = cmdSearch.createCommand(request, response, context);

			cmd.setName(outListName + "CmdSearch");
			outList.setAttribute("cmdSearch", cmd);
		}

		CommandInfo cmdBack = listing.getCommand(ListingDescriptor.COMMAND_BACK);

		if (cmdBack != null)
		{
			Command cmd = cmdBack.createCommand(request, response, context);

			outList.setAttribute("cmdBack", cmd);
		}
	}

	/**
	 * Create a page navigation command.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param name
	 *            The command name.
	 * @return The new command.
	 * @throws ModelException.
	 */
	private static Command createPageCommand(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					String name) throws ModelException
	{
		return createPageCommand(request, response, listing, name, null);
	}

	/**
	 * Create a page navigation command.
	 *
	 * @param request
	 *            The model request.
	 * @param response
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param name
	 *            The command name.
	 * @return The new command.
	 * @throws ModelException.
	 */
	private static Command createPageCommand(ModelRequest request, ModelResponse response, ListingDescriptor listing,
					String name, String[] ommitPrevParameters) throws ModelException
	{
		Command cmd = ModelTools.createPreviousModelCommand(request, response, listing.getListModel(),
						ommitPrevParameters);

		cmd.setName(name);

		return cmd;
	}

	/**
	 * Create the table header response elements.
	 *
	 * @param req
	 *            The model request.
	 * @param res
	 *            The model response.
	 * @param listing
	 *            The listing.
	 * @param outList
	 *            The list response element.
	 * @return The listing output element.
	 * @throws ModelException.
	 */
	private static Output createHeaderElements(ModelRequest req, ModelResponse res, ListingDescriptor listing,
					ListContext context) throws ModelException
	{
		Output outList = res.createOutput(context.getListName());

		res.add(outList);

		outList.setAttribute("columnCount", new Integer(listing.getVisibleColumnCount()
						+ (listing.getItemCommands() != null ? 1 : 0)));

		Output outHeader = res.createOutput("header");

		outList.setAttribute("header", outHeader);

		int colNum = 0;

		for (Iterator i = listing.columnIterator(); i.hasNext();)
		{
			ColumnDescriptor column = (ColumnDescriptor) i.next();

			Output outColumn = res.createOutput(String.valueOf(colNum++));

			outColumn.setContent(column.getAs());
			outColumn.setAttribute("label", column.getLabel());
			outColumn.setAttribute("bundle", column.getBundle() != null ? column.getBundle() : listing.getBundle());
			outColumn.setAttribute("width", new Integer(column.getWidth()));

			if (column.getSort() != SortOrder.NONE)
			{
				outColumn.setAttribute("sort", column.getSort() == SortOrder.ASCENDING ? "U" : "D");
			}

			if (! column.isVisible())
			{
				outColumn.setAttribute("hide", "Y");
			}

			if (column.isSortable() && ! "custom".equals(column.getViewer()) && ! column.getViewer().startsWith("js:"))
			{
				Command sortCommand = createPageCommand(req, res, listing, "sort" + colNum);

				sortCommand.setParameter(context.getListName() + "Sort", column.getName());
				outColumn.setAttribute("sortCommand", sortCommand);
			}

			outHeader.add(outColumn);
		}

		return outList;
	}
}
