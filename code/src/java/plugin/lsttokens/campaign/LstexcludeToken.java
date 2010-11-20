/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.campaign;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with LSTEXCLUDE Token
 */
public class LstexcludeToken extends AbstractTokenWithSeparator<Campaign>
		implements CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "LSTEXCLUDE";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		Campaign campaign, String value)
	{
		final StringTokenizer lstTok = new StringTokenizer(value, "|");

		while (lstTok.hasMoreTokens())
		{
			final String lstFilename = lstTok.nextToken();
			CampaignSourceEntry cse =
					context.getCampaignSourceEntry(campaign, lstFilename);
			if (cse == null)
			{
				//Error
				return ParseResult.INTERNAL_ERROR;
			}
			/*
			 * No need to check for cse.getIncludeItems or getExcludeItems as
			 * the use of pipe separator would have caused an error in fetching
			 * the CSE
			 */
			context.obj.addToList(campaign, ListKey.FILE_LST_EXCLUDE, cse);
		}

		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		Changes<CampaignSourceEntry> cseChanges =
				context.obj.getListChanges(campaign, ListKey.FILE_LST_EXCLUDE);
		Collection<CampaignSourceEntry> added = cseChanges.getAdded();
		if (added == null)
		{
			//empty indicates no token
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (CampaignSourceEntry cse : added)
		{
			set.add(cse.getLSTformat());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
