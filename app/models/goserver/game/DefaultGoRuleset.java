package models.goserver.game;

import models.goserver.game.rules.GoRuleset;
import models.goserver.game.rules.KoRule;
import models.goserver.game.rules.KomiRule;
import models.goserver.game.rules.SuicideRule;

/**
 * GoRuleset with all basic rules (SuicideRule, KoRule and KomiRule) added.
 * 
 * @author Kacper
 *
 */
public class DefaultGoRuleset {

	private static GoRuleset defaultRulesetInstance;

	/**
	 * Get a ruleset with default rules (Suicide rule, Ko rule and Komi rule)
	 * 
	 * @return Default ruleset object.
	 */
	public synchronized static GoRuleset getDefaultRuleset() {
		if (defaultRulesetInstance == null) {
			defaultRulesetInstance = new GoRuleset();
			defaultRulesetInstance.addRule(SuicideRule.getInstance());
			defaultRulesetInstance.addRule(KoRule.getInstance());
			defaultRulesetInstance.addRule(KomiRule.getInstance());
		}
		return defaultRulesetInstance;
	}

}
