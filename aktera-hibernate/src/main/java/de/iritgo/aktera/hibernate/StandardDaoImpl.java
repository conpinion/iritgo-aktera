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

package de.iritgo.aktera.hibernate;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.lang.InstantiationException;
import org.hibernate.*;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import de.iritgo.simplelife.constants.SortOrder;


@Transactional(readOnly = true)
public class StandardDaoImpl extends HibernateDaoSupport implements StandardDao
{
	public long countByNamedQuery(final String query, final Properties params)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				return countByQuery(session.getNamedQuery(query).getQueryString(), params);
			}
		});
	}

	public long countByNamedFindQuery(final String query, final Properties params)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				return countByFindQuery(session.getNamedQuery(query).getQueryString(), params);
			}
		});
	}

	public long countByQuery(final String query, final Properties params)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				return countByQuery(session.createQuery(query), params);
			}
		});
	}

	private Long countByQuery(Query query, Properties params)
	{
		for (String paramName : query.getNamedParameters())
		{
			Object value = params.get(paramName);

			if (value == null)
			{
				throw new QueryParameterException("Unable to find parameter '" + paramName + "'");
			}

			query.setParameter(paramName, params.get(paramName));
		}

		return (Long) query.uniqueResult();
	}

	public long countByFindQuery(final String query, final Properties params)
	{
		String countQuery = "select count (*) from" + query.substring(query.indexOf("from") + 4);

		return countByQuery(countQuery, params);
	}

	public List findByNamedQuery(final String query, final Properties params, final int firstResult,
					final int maxResults, final String orderBy, final SortOrder orderDir)
	{
		return (List) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				return findByQuery(session.getNamedQuery(query).getQueryString(), params, firstResult, maxResults,
								orderBy, orderDir);
			}
		});
	}

	public List findByQuery(final String query, final Properties params, final int firstResult, final int maxResults,
					final String orderBy, final SortOrder orderDir)
	{
		return (List) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query q = session.createQuery(query
								+ (orderBy != null ? " order by " + orderBy + " " + orderDir.hql() : ""));

				return findByQuery(q, params, firstResult, maxResults);
			}
		});
	}

	private List findByQuery(Query query, Properties params, int firstResult, int maxResults)
	{
		for (String paramName : query.getNamedParameters())
		{
			Object value = params.get(paramName);

			if (value == null)
			{
				throw new QueryParameterException("Unable to find parameter '" + paramName + "'");
			}

			query.setParameter(paramName, params.get(paramName));
		}

		query.setMaxResults(maxResults);
		query.setFirstResult(firstResult);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	public Object get(String entityName, Serializable id)
	{
		return getHibernateTemplate().get(entityName, id);
	}

	public Object newEntity(String entityName) throws InstantiationException, IllegalAccessException
	{
		Class klass = getSessionFactory().getClassMetadata(entityName).getMappedClass(EntityMode.POJO);

		return klass.newInstance();
	}

	@Transactional(readOnly = false)
	public void update(Object entity)
	{
		getHibernateTemplate().update(entity);
	}

	@Transactional(readOnly = false)
	public void create(Object entity)
	{
		getHibernateTemplate().saveOrUpdate(entity);
	}

	@Transactional(readOnly = false)
	public void delete(Object entity)
	{
		getHibernateTemplate().delete(entity);
	}
}
