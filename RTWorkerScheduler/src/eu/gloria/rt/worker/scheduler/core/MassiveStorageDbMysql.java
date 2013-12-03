package eu.gloria.rt.worker.scheduler.core;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import eu.gloria.rt.db.scheduler.ObservingPlan;
import eu.gloria.rt.db.scheduler.ObservingPlanState;
import eu.gloria.rt.db.scheduler.SchTimeFrame;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;
import eu.gloria.rt.worker.scheduler.interfaces.ConfigUpgradeable;
import eu.gloria.rt.worker.scheduler.interfaces.DataBaseInterface;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

/**
 * @author Alfredo
 * 
 *         Class to implements a data base interface and connect with MySQL data base server.
 */
public class MassiveStorageDbMysql implements DataBaseInterface, ConfigUpgradeable {
//	private EntityManagerFactory factory;
	private SchedulerContext schContext;
	private int daysScheduling;
	private SchedulerLog log;

	/**
	 * Constructor.
	 * 
	 * @param sc
	 *            The schedule context.
	 */
	public MassiveStorageDbMysql(SchedulerContext sc) {
		try {
			schContext = sc;
			log = sc.logger(getClass());
			
			/*Map<String, String> prop = new HashMap<String, String>();
			String url = "jdbc:mysql://" + sc.getDatabaseIp() + ":" + sc.getDatabasePort() + "/" + sc.getDatabaseDb() + "?autoReconnect=true";
			prop.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
			prop.put("hibernate.connection.url",          url);
			prop.put("hibernate.connection.username",     sc.getDatabaseUser());
			prop.put("hibernate.connection.password",     sc.getDatabasePass());
			factory = Persistence.createEntityManagerFactory("RTPersistence", prop);*/
			
//			factory = Persistence.createEntityManagerFactory("RTPersistence");
			
	
			schContext.addConfigUpgradeable(this);
			updateConfig();
		} catch (PersistenceException e) {
			log.info(schContext.language.getString("MsStDbMysql_Error_connect_db"));
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void updateConfig() {
		daysScheduling = schContext.getDaysScheduling();
	}

	@Override
	public ObservingPlan getNextOpToProcess() {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Get_next_op_to_process"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT op ");
			sb.append("FROM ObservingPlan op ");
			sb.append("WHERE op.state = :state ");
			sb.append("  AND op.advertDeadline >= :now ");
			sb.append("ORDER BY op.priority DESC");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query, put the parameters and set the result size
			ObservingPlanState stateParam = ObservingPlanState.ADVERT_QUEUED;
			Timestamp nowParam = new Timestamp(System.currentTimeMillis());
			Query query = em.createQuery(sql);
			query.setParameter("state", stateParam);
			query.setParameter("now", nowParam);
			query.setMaxResults(1);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "state", stateParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "now", nowParam));

			// Request the result and return it
			@SuppressWarnings("unchecked")
			List<ObservingPlan> list = (List<ObservingPlan>) query.getResultList();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Results"), list.size()));
			
			DBUtil.commit(em);
			
			if (list.size() == 0) {
				return null;
			} else {
				return list.get(0);
			}
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return null;
	}

	@Override
	public void resetAllOpsRunningToQueued() {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Reset_all_ops_running_to_queued"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ObservingPlan op ");
			sb.append("SET   op.state = :newState ");
			sb.append("WHERE op.state = :oldState ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			ObservingPlanState newStateParam = ObservingPlanState.ADVERT_QUEUED;
			ObservingPlanState oldStateParam = ObservingPlanState.ADVERT_RUNNING;
			Query query = em.createQuery(sql);
			query.setParameter("newState", newStateParam);
			query.setParameter("oldState", oldStateParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "newState", newStateParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "oldState", oldStateParam));

			// Execute the query and do commit to the data base
			query.executeUpdate();
			
			
			 DBUtil.commit(em);
			 
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			 DBUtil.rollback(em);
		} finally {
			 DBUtil.close(em);
		}
	}

	@Override
	public void rejectOpsPassedAdvertDeadLine() {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Modify_op_in_dead_line"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ObservingPlan op ");
			sb.append("SET op.state   = :newState, ");
			sb.append("    op.comment = :comment ");
			sb.append("WHERE op.advertDeadline <= :now ");
			sb.append("  AND op.state = :oldState ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the parameters
			ObservingPlanState newStateParam = ObservingPlanState.ADVERT_REJECTED;
			Timestamp nowParam = new Timestamp(System.currentTimeMillis());
			String commentParam = schContext.language.getString("MsStDbMysql_Dead_line");
			ObservingPlanState oldStateParam = ObservingPlanState.ADVERT_QUEUED;

			// Make the query
			Query query = em.createQuery(sql);
			query.setParameter("newState", newStateParam);
			query.setParameter("now", nowParam);
			query.setParameter("comment", commentParam);
			query.setParameter("oldState", oldStateParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "newState", newStateParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "now", nowParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "comment", commentParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "oldState", oldStateParam));

			// Execute the UPDATE query, with commit after that
			int num = query.executeUpdate();
			
			DBUtil.commit(em);

			log.debug(String.format(schContext.language.getString("MsStDbMysql_Ops_in_dead_line"), num));
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			 DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public void setOpScheduling(BigInteger id) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(String.format(schContext.language.getString("MsStDbMysql_Set_op_scheduling"), id));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ObservingPlan op ");
			sb.append("SET op.state         = :state, ");
			sb.append("    op.advertDateIni = :now ");
			sb.append("WHERE op.id = :id ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the parameters
			ObservingPlanState stateParam = ObservingPlanState.ADVERT_RUNNING;
			Timestamp nowParam = new Timestamp(System.currentTimeMillis());

			// Make the query
			Query query = em.createQuery(sql);
			query.setParameter("state", stateParam);
			query.setParameter("now", nowParam);
			query.setParameter("id", id.longValue());

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "state", stateParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "now", nowParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "id", id));

			// Execute the UPDATE query, with commit after that
			query.executeUpdate();
			
			DBUtil.commit(em);
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public void setOpScheduled(BigInteger id, Map<String, Object> modifies) {
		EntityManager em =  DBUtil.getEntityManager();
		try {
			log.info(String.format(schContext.language.getString("MsStDbMysql_Op_scheduled"), id));
			
			 DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ObservingPlan op SET");
			for (Iterator<Map.Entry<String, Object>> it = modifies.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Object> entry = it.next();
				sb.append(" op." + entry.getKey() + " = :" + entry.getKey());
				if (it.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(" WHERE op.id = :id ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// It is a UPDATE query, create a entity transaction
			// Make the query with them parameters y los mostramos por el log
			Query query = em.createQuery(sql);
			for (Map.Entry<String, Object> entry : modifies.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				try{
					query.setParameter(key, value);
				}catch(Exception ex){
					log.error("Inserting param: [key=" + key + ", value=" + value + "]. " + ex.getMessage());
					throw ex;
				}
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), key, value));
			}

			query.setParameter("id", id.longValue());
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "id", id));

			// Execute the query and do commit to the data base
			query.executeUpdate();
			
			DBUtil.commit(em);
			
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Saved_op_scheduled"), id));
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public void setUuidOp(String uuidOp, Timestamp iniOp, Timestamp endOp) {
		EntityManager em = DBUtil.getEntityManager();
		final int MARGIN = 0; // FIXME 2.5 seconds
		try {
			log.info(schContext.language.getString("MsStDbMysql_Set_uuid_op"));
			
			DBUtil.beginTransaction(em);

			// Truncate the timestamps to 0 milliseconds
			iniOp = truncateTimestamp(iniOp);
			endOp = truncateTimestamp(endOp);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM SchTimeFrame stf ");
			sb.append("WHERE dateIni <= :dateIni ");
			sb.append("  AND dateEnd >= :dateEnd ");
			sb.append("  AND uuidOp IS NULL");

			// Show the SQL in the log
			String sql = sb.toString();

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("dateIni", iniOp);
			query.setParameter("dateEnd", endOp);
			query.setMaxResults(1);

			// Show the parameters in the log

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> listStf = (List<SchTimeFrame>) query.getResultList();
			if (listStf.size() == 0) {
				throw new Exception(schContext.language.getString("MsStDbMysql_Not_find_slot"));
				
			}else{
				SchTimeFrame stf = listStf.get(0);
				Timestamp iniSlot = truncateTimestamp(stf.getDateIni());
				Timestamp endSlot = truncateTimestamp(stf.getDateEnd());
				
				SchTimeFrame[] resSTF = new SchTimeFrame[3];
				
				// Previous slot
				if (Math.abs(iniSlot.getTime() - iniOp.getTime())  >  MARGIN) {
					resSTF[0] = new SchTimeFrame();
					resSTF[0].setDateIni(iniSlot);
					resSTF[0].setDateEnd(iniOp);
				}
				
				// OP slot
				resSTF[1] = new SchTimeFrame();
				resSTF[1].setDateIni(iniOp);
				resSTF[1].setDateEnd(endOp);
				resSTF[1].setUuidOp(uuidOp);
				
				// Later slot
				if (Math.abs(endSlot.getTime() - endOp.getTime())  >  MARGIN) {
					resSTF[2] = new SchTimeFrame();
					resSTF[2].setDateIni(endOp);
					resSTF[2].setDateEnd(endSlot);
				}
				
				// Delete the old free slot
				sb = new StringBuffer();
				sb.append("DELETE FROM SchTimeFrame stf ");
				sb.append("WHERE dateIni <= :dateIni ");
				sb.append("  AND dateEnd >= :dateEnd ");
				sb.append("  AND uuidOp IS NULL");

				// Show the SQL in the log
				sql = sb.toString();
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));
//System.out.println("TEST::deleting TimeFrame:" + stf.getDateIni() + " <-> " + stf.getDateEnd() );

				// Make the query with them parameters
				query = em.createQuery(sql);
				query.setParameter("dateIni", stf.getDateIni());
				query.setParameter("dateEnd", stf.getDateEnd());

				// Show the parameters in the log
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateIni", stf.getDateIni()));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateEnd", stf.getDateEnd()));

				// Execute the query
				query.executeUpdate();
				
				// Save the new slots
				for (int i=0 ; i<resSTF.length ; i++) {
					if (resSTF[i] != null) {
//System.out.println("TEST::adding TimeFrame:" + resSTF[i].getDateIni() + " <-> " + resSTF[i].getDateEnd() + "-->OPUUID=" + resSTF[i].getUuid());
						saveTimeFrameInDb(resSTF[i].getDateIni(), resSTF[i].getDateEnd(), resSTF[i].getUuid(), em); 
					}
				}
			}
			
			DBUtil.commit(em);
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			
			DBUtil.rollback(em);
			 
			for (StackTraceElement ste : e.getStackTrace()) {
				log.fatal(ste.toString());
			}
			
		} finally {
			
			 DBUtil.close(em);
		}
	}
	
	public void setUuidOp(String uuidOp, Timestamp iniOp, Timestamp endOp, boolean ant) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Set_uuid_op"));
			
			DBUtil.beginTransaction(em);

			// Truncate the timestamps to 0 milliseconds
			iniOp = truncateTimestamp(iniOp);
			endOp = truncateTimestamp(endOp);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM SchTimeFrame stf ");
			sb.append("WHERE dateIni <= :dateIni ");
			sb.append("  AND dateEnd >= :dateEnd ");
			sb.append("  AND uuidOp IS NULL");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("dateIni", iniOp);
			query.setParameter("dateEnd", endOp);
			query.setMaxResults(1);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateIni", iniOp));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateEnd", endOp));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> listStf = (List<SchTimeFrame>) query.getResultList();
			if (listStf.size() > 0) {
				// Take the SchTimeFrame that exist in the database that keeps them as busy
				SchTimeFrame stfBusy = listStf.get(0);

				// Make the update query
				sb = new StringBuffer();
				sb.append("UPDATE SchTimeFrame stf ");
				sb.append("SET dateIni = :iniOp, ");
				sb.append("    dateEnd = :endOp, ");
				sb.append("    uuidOp  = :uuidOp ");
				sb.append("WHERE dateIni = :dateIni ");
				sb.append("  AND dateEnd = :dateEnd ");
				sb.append("  AND uuidOp IS NULL ");

				// Show the query in the log
				sql = sb.toString();
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

				// Insert the parameters
				Timestamp dateIniParam = stfBusy.getDateIni();
				Timestamp dateEndParam = stfBusy.getDateEnd();
				query = em.createQuery(sql);
				query.setParameter("iniOp", iniOp);
				query.setParameter("endOp", endOp);
				query.setParameter("uuidOp", uuidOp);
				query.setParameter("dateIni", dateIniParam);
				query.setParameter("dateEnd", dateEndParam);

				// Show the parameters in the log
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "iniOp", iniOp));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "endOp", endOp));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "uuidOp", uuidOp));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateIni", dateIniParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateEnd", dateEndParam));

				// Has a free time before the OP?
				if (dateIniParam.getTime() != iniOp.getTime()) {
					// ...make this free time
					SchTimeFrame stfFree1 = new SchTimeFrame();
					stfFree1.setDateIni(dateIniParam);
					stfFree1.setDateEnd(iniOp);
					stfFree1.setUuidOp(null);

					em.persist(stfFree1);
				}

				// Has a free time after the OP?
				if (dateEndParam.getTime() != endOp.getTime()) {
					// ...make this free time
					SchTimeFrame stfFree2 = new SchTimeFrame();
					stfFree2.setDateIni(endOp);
					stfFree2.setDateEnd(dateEndParam);
					stfFree2.setUuidOp(null);

					em.persist(stfFree2);
				}

				// execute
				query.executeUpdate();
				
				DBUtil.commit(em);
			}
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public void setOpError(BigInteger id, String comment) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Set_error_op"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ObservingPlan op ");
			sb.append("SET   op.state = :newState, ");
			sb.append("      op.comment = :comment ");
			sb.append("WHERE op.id = :id ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			ObservingPlanState newStateParam = ObservingPlanState.ADVERT_ERROR;
			Query query = em.createQuery(sql);
			query.setParameter("newState", newStateParam);
			query.setParameter("comment", comment);
			query.setParameter("id", id.longValue());

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "newState", newStateParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "comment", comment));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "id", id));

			// Execute the query and do commit to the data base
			query.executeUpdate();
			
			DBUtil.commit(em);
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public SchTimeFrame getMaxSlotSchTimeFrame() {
		EntityManager em =  DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Get_max_sch_time_frame"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM SchTimeFrame stf ");
			sb.append("ORDER BY dateIni DESC ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setMaxResults(1);

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> list = (List<SchTimeFrame>) query.getResultList();
			
			DBUtil.commit(em);
			
			return list.size() >= 1 ? list.get(0) : null;
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return null;
	}

	@Override
	public void deleteOldsSlots() {
		
		EntityManager em = DBUtil.getEntityManager();
		
		Timestamp nowParam = new Timestamp(System.currentTimeMillis());
		try {
			log.info(schContext.language.getString("MsStDbMysql_Delete_previous_sch_time_frame"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM SchTimeFrame stf ");
			sb.append("WHERE dateEnd <= :now ");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("now", nowParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "now", nowParam));

			// Execute the query
			query.executeUpdate();
			
			DBUtil.commit(em);

		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			
			 DBUtil.rollback(em);
			 
		} finally {
			
			DBUtil.close(em);
		}
	}

	@Override
	public void saveSchedulerTimeFrames(List<TimeFrame> lstTimeFrame) {
		for (TimeFrame tf : lstTimeFrame) {
			Timestamp ini = new Timestamp(tf.getInit().getTime());
			Timestamp end = new Timestamp(tf.getEnd().getTime());
			saveTimeFrameInDb(ini, end, null);
		}
	}

	/**
	 * This method save in the database a new SchTimeFrame.
	 * 
	 * @param ini
	 *            Initial time in the SchTimeFrame.
	 * @param end
	 *            Final time in the SchTimeFrame.
	 */
	private void saveTimeFrameInDb(Timestamp ini, Timestamp end, String uuidOp) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			
			DBUtil.beginTransaction(em);
			
			// Truncate the timestamps to 0 milliseconds
			ini = truncateTimestamp(ini);
			end = truncateTimestamp(end);

			// Show the parameters in the log
			log.info(String.format(schContext.language.getString("MsStDbMysql_Save_time_frame"), ini, end, uuidOp));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateIni", ini));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateEnd", end));

			// I create the object and set the data
			SchTimeFrame sft = new SchTimeFrame();
			sft.setDateIni(ini);
			sft.setDateEnd(end);
			sft.setUuidOp(uuidOp);

			// Save object in the persistence and send the commit
			em.persist(sft);
			
			DBUtil.commit(em);
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}
	
	/**
	 * This method save in the database a new SchTimeFrame.
	 * 
	 * @param ini
	 *            Initial time in the SchTimeFrame.
	 * @param end
	 *            Final time in the SchTimeFrame.
	 * @throws Exception 
	 */
	private void saveTimeFrameInDb(Timestamp ini, Timestamp end, String uuidOp, EntityManager em) throws Exception {
		try {
			// Truncate the timestamps to 0 milliseconds
			ini = truncateTimestamp(ini);
			end = truncateTimestamp(end);

			// Show the parameters in the log
			log.info(String.format(schContext.language.getString("MsStDbMysql_Save_time_frame"), ini, end, uuidOp));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateIni", ini));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "dateEnd", end));

			// I create the object and set the data
			SchTimeFrame sft = new SchTimeFrame();
			sft.setDateIni(ini);
			sft.setDateEnd(end);
			sft.setUuidOp(uuidOp);

			// Save object in the persistence and send the commit
			em.persist(sft);
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			throw e;
		} 
	}

	@Override
	public int getNumOpScheduledUser(Date date, String user) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(String.format(schContext.language.getString("MsStDbMysql_Get_num_op_scheduled_by_user"), user));
			
			DBUtil.beginTransaction(em);

			// Make the initial and end time of the day
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			gc.set(Calendar.HOUR_OF_DAY, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			Timestamp iniParam = new Timestamp(gc.getTimeInMillis());
			gc.add(Calendar.DAY_OF_YEAR, 1);
			Timestamp endParam = new Timestamp(gc.getTimeInMillis());

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT op ");
			sb.append("FROM ObservingPlan op ");
			sb.append("WHERE predAstr >= :ini ");
			sb.append("  AND predAstr <= :end ");
			sb.append("  AND user = :user");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("ini", iniParam);
			query.setParameter("end", endParam);
			query.setParameter("user", user);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ini", iniParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "end", endParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "user", user));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<ObservingPlan> lst = (List<ObservingPlan>) query.getResultList();
			
			DBUtil.commit(em);
			
			return lst.size();
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return 0;
	}

	public long getTimeScheduledUser(Date date, String user) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Get_time_op_scheduled"));
			
			DBUtil.beginTransaction(em);

			// Make the initial and end time of the day
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			gc.set(Calendar.HOUR_OF_DAY, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			Timestamp iniParam = new Timestamp(gc.getTimeInMillis());
			gc.add(Calendar.DAY_OF_YEAR, 1);
			Timestamp endParam = new Timestamp(gc.getTimeInMillis());

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT SUM(op.predDuration) ");
			sb.append("FROM   ObservingPlan op ");
			sb.append("WHERE  op.predAstr >= :ini ");
			sb.append("   AND op.predAstr <= :end ");
			sb.append("   AND op.user = :user");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("ini", iniParam);
			query.setParameter("end", endParam);
			query.setParameter("user", user);
			query.setMaxResults(1);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ini", iniParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "end", endParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "user", user));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<Long> list = (List<Long>) query.getResultList();
			Long result = list.get(0);
			
			DBUtil.commit(em);
			
			if (list.size() == 0 || result == null) {
				return 0;
			} else {
				return result;
			}
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return 0;
	}

	@Override
	public int getNumOpScheduled(Date date) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Get_num_op_scheduled"));
			
			DBUtil.beginTransaction(em);

			// Make the initial and end time of the day
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			gc.set(Calendar.HOUR_OF_DAY, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			Timestamp iniParam = new Timestamp(gc.getTimeInMillis());
			gc.add(Calendar.DAY_OF_YEAR, 1);
			Timestamp endParam = new Timestamp(gc.getTimeInMillis());

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT op ");
			sb.append("FROM ObservingPlan op ");
			sb.append("WHERE predAstr >= :ini ");
			sb.append("  AND predAstr <= :end");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("ini", iniParam);
			query.setParameter("end", endParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ini", iniParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "end", endParam));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<ObservingPlan> lst = (List<ObservingPlan>) query.getResultList();
			
			DBUtil.commit(em);
			
			return lst.size();
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return 0;
	}

	@Override
	public long getTimeScheduled(Date date) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Get_time_op_scheduled"));
			
			DBUtil.beginTransaction(em);

			// Make the initial and end time of the day
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			gc.set(Calendar.HOUR_OF_DAY, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			Timestamp iniParam = new Timestamp(gc.getTimeInMillis());
			gc.add(Calendar.DAY_OF_YEAR, 1);
			Timestamp endParam = new Timestamp(gc.getTimeInMillis());

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT SUM(op.predDuration) ");
			sb.append("FROM   ObservingPlan op ");
			sb.append("WHERE  op.predAstr >= :ini ");
			sb.append("   AND op.predAstr <= :end");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("ini", iniParam);
			query.setParameter("end", endParam);
			query.setMaxResults(1);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ini", iniParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "end", endParam));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<Long> list = (List<Long>) query.getResultList();
			Long result = list.get(0);
			
			DBUtil.commit(em);
			
			if (list.size() == 0 || result == null) {
				return 0;
			} else {
				return result;
			}
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			 DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return 0;
	}

	@Override
	public List<SchTimeFrame> getSlotsWithPriority(Timestamp init, int priority) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(String.format(schContext.language.getString("MsStDbMysql_Get_slots_with_priority"), priority));
			
			DBUtil.beginTransaction(em);

			// Make the initial and end time of the day
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(System.currentTimeMillis());
			gc.add(Calendar.DAY_OF_YEAR, daysScheduling);
			gc.set(Calendar.MILLISECOND, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.HOUR_OF_DAY, 0);
			Timestamp maxDayParam = new Timestamp(gc.getTimeInMillis());

			// Make the SQL with the free slots
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM   SchTimeFrame stf ");
			sb.append("WHERE  dateEnd > :init ");
			sb.append("  AND  dateIni < :max ");
			sb.append("  AND  uuidOp IS NULL ");
			sb.append("ORDER BY  dateIni ASC");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			query.setParameter("init", init);
			query.setParameter("max", maxDayParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "init", init));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "max", maxDayParam));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> lst1 = mergeSchTimeFrames((List<SchTimeFrame>) query.getResultList());

			// Make the SQL with the occupied slots with OP with minor priority
			sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM   SchTimeFrame stf ");
			sb.append("WHERE  dateEnd > :init ");
			sb.append("  AND  dateIni < :max ");
			sb.append("  AND  (uuidOp IS NULL OR ");
			sb.append("        uuidOp IN (SELECT  uuid  FROM ObservingPlan op  WHERE priority < :priority AND offeredDate IS NULL))");
			sb.append("ORDER BY dateIni ASC");

			// Show the SQL in the log
			sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			query = em.createQuery(sql);
			query.setParameter("init", init);
			query.setParameter("priority", priority);
			query.setParameter("max", maxDayParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "init", init));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "priority", priority));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "max", maxDayParam));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> lst2 = mergeSchTimeFrames((List<SchTimeFrame>) query.getResultList());

			// Merge the lists
			for (SchTimeFrame stf : lst2) {
				// Only add if the slots are not in the list yet
				if (!lst1.contains(stf)) {
					lst1.add(stf);
				}
			}
			
			 DBUtil.commit(em);

			return lst1;
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return new LinkedList<SchTimeFrame>();
	}

	@Override
	public List<SchTimeFrame> mergeSchTimeFrames(List<SchTimeFrame> stfs) {
		try {
			log.info(schContext.language.getString("MsStDbMysql_Merge_sch_time_frames"));
			if (stfs == null || stfs.size() == 0) {
				return new LinkedList<SchTimeFrame>();
			}

			List<SchTimeFrame> res = new LinkedList<SchTimeFrame>();
			// Iterate from the first to penultimate
			while (stfs.size() > 1) {
				SchTimeFrame act = stfs.get(0);
				SchTimeFrame nxt = stfs.get(1);

				long timeActEnd = act.getDateEnd().getTime();
				long timeNxtIni = nxt.getDateIni().getTime();

				// Create the initial time of the day
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTimeInMillis(timeNxtIni);
				gc.set(Calendar.MILLISECOND, 0);
				gc.set(Calendar.SECOND, 0);
				gc.set(Calendar.MINUTE, 0);
				gc.set(Calendar.HOUR_OF_DAY, 0);
				long timeInitDay = gc.getTimeInMillis();

				// If it is not the initial time or the time are equal
				if (timeActEnd != timeNxtIni || timeNxtIni == timeInitDay) {
					act.setUuidOp(null);
					res.add(act);
					stfs.remove(0);

				} else {
					nxt.setUuidOp(null);
					nxt.setDateIni(act.getDateIni());
					stfs.remove(0);
				}
			}

			// Add the last and finish
			SchTimeFrame stf = stfs.get(0);
			stf.setUuidOp(null);
			res.add(stf);
			return res;
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
		}
		return new LinkedList<SchTimeFrame>();
	}

	@Override
	public List<SchTimeFrame> searchSchTimeFramesCrasheds(long ini, long end) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Looking_for_sch_time_frames_crasheds"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM   SchTimeFrame stf ");
			sb.append("WHERE  uuidOp IS NOT NULL");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> lstDb = (List<SchTimeFrame>) query.getResultList();
			List<SchTimeFrame> lst = new LinkedList<SchTimeFrame>();

			// Seek if the times are crashed
			for (SchTimeFrame stf : lstDb) {
				if (stf.getDateEnd().getTime() > ini && stf.getDateIni().getTime() < end) {
					lst.add(stf);
				}
			}
			
			DBUtil.commit(em);
			
			return lst;
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return new LinkedList<SchTimeFrame>();
	}

	@Override
	public void resetOps(List<SchTimeFrame> stfs) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Resets_ops"));
			
			DBUtil.beginTransaction(em);

			for (SchTimeFrame stf : stfs) {
				// Make the SQL
				StringBuffer sb = new StringBuffer();
				sb.append("UPDATE ObservingPlan op ");
				sb.append("SET   op.state        = :newState, ");
				sb.append("      op.comment      = :comment, ");
				sb.append("      op.predAstr     = NULL, ");
				sb.append("      op.predDuration = 0 ");
				sb.append("WHERE op.uuid = :uuid");

				// Show the SQL in the log
				String sql = sb.toString();
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

				// It is a UPDATE query, create a entity transaction

				// Make the query with them parameters
				ObservingPlanState newStateParam = ObservingPlanState.ADVERT_QUEUED;
				String commentParam = schContext.language.getString("MsStDbMysql_Op_re_scheduling_for_priority");
				String idParam = stf.getUuid();
				Query query = em.createQuery(sql);
				query.setParameter("newState", newStateParam);
				query.setParameter("comment", commentParam);
				query.setParameter("uuid", idParam);

				// Show the parameters in the log
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "newState", newStateParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "comment", commentParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "uuid", idParam));

				// Execute the query and do commit to the data base
				query.executeUpdate();

				DBUtil.commit(em);
			}
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public void resetSchTimeFrames(List<SchTimeFrame> stfs) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Resets_sch_time_frames"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append(" FROM SchTimeFrame stf WHERE ");
			for (int i = 0; i < stfs.size(); i++) {
				sb.append("dateIni=:ii" + i + " OR dateEnd=:ei" + i + " OR ");
				sb.append("dateIni=:ie" + i + " OR dateEnd=:ee" + i);

				if (i + 1 < stfs.size()) {
					sb.append(" OR ");
				}
			}

			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), "SELECT stf" + sql));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), "DELETE" + sql));
			Query querySelect = em.createQuery("SELECT stf" + sql);
			Query queryDelete = em.createQuery("DELETE" + sql);

			for (int i = 0; i < stfs.size(); i++) {
				SchTimeFrame stf = stfs.get(i);

				Timestamp iniParam = stf.getDateIni();
				Timestamp endParam = stf.getDateEnd();

				// Make the parameters
				querySelect.setParameter("ii" + i, iniParam);
				querySelect.setParameter("ei" + i, iniParam);
				querySelect.setParameter("ie" + i, endParam);
				querySelect.setParameter("ee" + i, endParam);

				queryDelete.setParameter("ii" + i, iniParam);
				queryDelete.setParameter("ei" + i, iniParam);
				queryDelete.setParameter("ie" + i, endParam);
				queryDelete.setParameter("ee" + i, endParam);

				// Show the parameters in the log
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ii" + i, iniParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ei" + i, iniParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ie" + i, endParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "ee" + i, endParam));
			}

			// Request with a SELECT query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> resultado = (List<SchTimeFrame>) querySelect.getResultList();

			// Delete with a DELETE query
			queryDelete.executeUpdate();
			
			// Merge the selected STFs
			List<SchTimeFrame> stfMerged = mergeSchTimeFrames(resultado);

			// And save in the data base
			for (SchTimeFrame stf : stfMerged) {
				saveTimeFrameInDb(stf.getDateIni(), stf.getDateEnd(), stf.getUuid(), em);
			}
			
			DBUtil.commit(em);
			 
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public void updateOpCanceledByGloria() {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Update_op_canceled_by_gloria"));
			
			DBUtil.beginTransaction(em);

			// First: seek its
			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM   SchTimeFrame stf ");
			sb.append("WHERE  uuidOp IN (SELECT op.uuid  FROM ObservingPlan op ");
			sb.append("                  WHERE  scheduleDateIni > :now  AND  state = :abotedState)");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);
			Timestamp nowParam = new Timestamp(System.currentTimeMillis());
			ObservingPlanState abotedStateParam = ObservingPlanState.ABORTED;
			query.setParameter("now", nowParam);
			query.setParameter("abotedState", abotedStateParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "now", nowParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "abotedState", abotedStateParam));

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> stfs = (List<SchTimeFrame>) query.getResultList();

			if (stfs.size() > 0) {
				// Reset the STFs returned
				resetSchTimeFrames(stfs);

				// Second: Update the table of the OPs
				// Make the SQL
				sb = new StringBuffer();
				sb.append("UPDATE ObservingPlan op ");
				sb.append("SET    scheduleDateIni = NULL, scheduleDateEnd = NULL ");
				sb.append("WHERE  scheduleDateIni > :now  AND  state = :abotedState");

				// Show the SQL in the log
				sql = sb.toString();
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

				// Make the query with them parameters
				query = em.createQuery(sql);
				query.setParameter("now", nowParam);
				query.setParameter("abotedState", abotedStateParam);

				// Show the parameters in the log
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "now", nowParam));
				log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "abotedState", abotedStateParam));

				// Execute the query and do commit to the data base
				query.executeUpdate();
				
			}
			
			DBUtil.commit(em);
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	@Deprecated
	public void rejectForOverbooking(ObservingPlan op) {
		EntityManager em = DBUtil.getEntityManager();
		try {
			log.info(schContext.language.getString("MsStDbMysql_Reject_for_overbooking"));
			
			DBUtil.beginTransaction(em);

			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ObservingPlan op ");
			sb.append("SET   op.state = :newState ");
			sb.append("WHERE op.id = :id");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			ObservingPlanState stateParam = ObservingPlanState.ADVERT_REJECTED;
			Query query = em.createQuery(sql);
			String idParam = String.valueOf(op.getId());
			query.setParameter("newState", stateParam);
			query.setParameter("id", idParam);

			// Show the parameters in the log
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "newState", stateParam));
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_param"), "id", idParam));

			// Execute the query and do commit to the data base
			query.executeUpdate();
			
			DBUtil.commit(em);
			
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
	}

	@Override
	public List<SchTimeFrame> getSlotsFree() {
		EntityManager em = DBUtil.getEntityManager();
		try {
			
			DBUtil.beginTransaction(em);
			
			// Make the SQL
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT stf ");
			sb.append("FROM   SchTimeFrame stf ");
			sb.append("WHERE  uuidOp IS NOT NULL ");
			sb.append("ORDER BY  dateIni ASC");

			// Show the SQL in the log
			String sql = sb.toString();
			log.debug(String.format(schContext.language.getString("MsStDbMysql_Sql_to_executing"), sql));

			// Make the query with them parameters
			Query query = em.createQuery(sql);

			// Execute the query
			@SuppressWarnings("unchecked")
			List<SchTimeFrame> lst1 = (List<SchTimeFrame>) query.getResultList();
			
			DBUtil.commit(em);

			return lst1;
		} catch (Exception e) {
			log.fatal(String.format(schContext.language.getString("MsStDbMysql_Error_executing_sql"), e.getMessage()));
			e.printStackTrace();
			DBUtil.rollback(em);
		} finally {
			DBUtil.close(em);
		}
		return new LinkedList<SchTimeFrame>();
	}
	
	private Timestamp truncateTimestamp(Timestamp ts) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(ts.getTime());
		gc.set(Calendar.MILLISECOND, 0);
		return new Timestamp(gc.getTimeInMillis());
	}
}
