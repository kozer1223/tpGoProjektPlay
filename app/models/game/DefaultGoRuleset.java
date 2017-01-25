package models.game;

import models.game.rules.GoRuleset;
import models.game.rules.KoRule;
import models.game.rules.KomiRule;
import models.game.rules.SuicideRule;

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
