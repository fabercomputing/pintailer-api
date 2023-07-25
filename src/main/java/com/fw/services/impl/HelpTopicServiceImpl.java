package com.fw.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IHelpTopicManager;
import com.fw.domain.HelpTopics;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IHelpTopicService;

@Service
public class HelpTopicServiceImpl implements IHelpTopicService {

	@Autowired
	IHelpTopicManager helpTopicManager;

	@Override
	@Transactional
	public HelpTopics persistHelpTopicsInfo(HelpTopics logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return helpTopicManager.persistHelpTopicsInfo(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateHelpTopicsById(HelpTopics logEntity) throws APIExceptions {
		if (logEntity != null) {
			helpTopicManager.updateHelpTopicsById(logEntity);
		}
	}

	@Override
	@Transactional
	public void deleteHelpTopicsById(HelpTopics id) throws APIExceptions {
		helpTopicManager.deleteHelpTopicsById(id);
	}

	@Override
	public List<HelpTopics> getAllHelpTopicsRowMapper() throws APIExceptions {
		List<HelpTopics> allHelpTopics = helpTopicManager
				.getAllHelpTopicsRowMapper();
		return getNestedChildren(allHelpTopics);
	}

	@Override
	public HelpTopics getHelpTopicsById(int Id) throws APIExceptions {
		return helpTopicManager.getHelpTopicsById(Id);
	}

	@Override
	public List<HelpTopics> getHelpTopicsHierarchy() throws APIExceptions {
		List<HelpTopics> helpTopics = getAllHelpTopicsRowMapper();
		List<HelpTopics> helpTopicsWithHierarchy = new ArrayList<HelpTopics>();
		for (HelpTopics helpTopic : helpTopics) {
			List<String> helpTopicsHierarchy = helpTopicManager
					.getHelpTopicsHierarchy(helpTopic.getTopicId());
			if (null == helpTopicsHierarchy || helpTopicsHierarchy.isEmpty()) {
				throw new APIExceptions(
						"Some error occured while retrieving the help topics hierarchy for the title ["
								+ helpTopic.getTitle() + "]");
			}
			helpTopic.setHierarchy(helpTopicsHierarchy.get(0));
			helpTopicsWithHierarchy.add(helpTopic);
		}
		return helpTopicsWithHierarchy;
	}

	private List<HelpTopics> getNestedChildren(List<HelpTopics> helpTopics)
			throws APIExceptions {

		Map<Integer, HelpTopics> helpTopicsMap = new HashMap<>();
		HelpTopics childHelpTopic = null;
		HelpTopics parentHelpTopic = null;

		// Here the help topic hierarchy is getting created as each help topic
		// which has
		// a parent is considered as its child and is pushed under its parent
		// help topic bean as child
		for (HelpTopics item : helpTopics) {
			// ------ Process child ----
			// the map is used to store all the help topics so that immediate
			// parent
			// can be search for a help topic in the loop
			if (!helpTopicsMap.containsKey(item.getTopicId())) {
				helpTopicsMap.put(item.getTopicId(), item);
			}
			childHelpTopic = helpTopicsMap.get(item.getTopicId());

			// ------ Process Parent ----
			// If a help topic is not a top level help topic, it will be a child
			// of its
			// parent help topic as per the DB structure.
			if (item.getTopicParentId() != 0) {
				// In case the help topics are not in the order i.e. expected
				// order is parent comes first and child later but can be fetch
				// in random, the below 'if' condition is added to add
				// the parent help topic which has not yet accessed in the loop
				// to maintain the loop logic and to avoid the extra order by
				// condition in the query. Also the order by condition in the
				// query is not a full proof solution as help topics addition
				// does not ensure than parent will always getting added before
				// the child
				if (!helpTopicsMap.containsKey(item.getTopicParentId())) {
					helpTopicsMap.put(item.getTopicParentId(), helpTopicManager
							.getHelpTopicsById(item.getTopicParentId()));
				}

				// Now fetching the parent help topic bean and adding the
				// current
				// item as its child
				parentHelpTopic = helpTopicsMap.get(item.getTopicParentId());
				parentHelpTopic.getChildren().add(childHelpTopic);
			}

		}

		// The below loop is to return the top most parents help topics in the
		// list
		// as all their respective children has been added in their beans.
		List<HelpTopics> helpTopicsTree = new ArrayList<HelpTopics>();
		for (HelpTopics helpTop : helpTopicsMap.values()) {
			if (helpTop.getTopicParentId() == 0) {
				helpTopicsTree.add(helpTop);
			}

		}
		return helpTopicsTree;
	}

}
