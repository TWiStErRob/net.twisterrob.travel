package net.twisterrob.blt.diff;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public class HtmlDiff {
	public String diff(String oldText, String newText) {
		diff_match_patch differ = new diff_match_patch();
		LinkedList<Diff> diff = differ.diff_main(oldText, newText);
		differ.diff_cleanupSemantic(diff);
		return differ.diff_prettyHtml(diff);
	}
}
