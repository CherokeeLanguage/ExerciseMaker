package com.cherokeelessons.eim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.cherokeelessons.eim.ChallengeResponsePair.ResponseLayout;
import com.cherokeelessons.log.Log;
import com.cherokeelessons.lyx.LyxTemplate;

public class App implements Runnable {
	private String inFolder;
	private String outFolder;
	private Map<String, ReplacementSet> randomReplacements = new HashMap<>();
	private Pragma pragma = null;
	private final Logger log = Log.getLogger(this);

	public App(String[] args) {
	}

	@Override
	public void run() {
		try {
			_run();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void _run() throws IOException {
		initFolders();

		List<ChallengeResponsePair> challenges;
		List<ChallengeResponsePair> queued;

		Collection<File> cfiles = FileUtils.listFiles(new File(inFolder), null, false);
		List<File> files = new ArrayList<>(cfiles);
		Collections.sort(files);
		for (File file : files) {
			pragma=new Pragma();
			System.out.println("File: "+file.getName());
			File outFile = new File(outFolder, file.getName());
			File debugOutFile = new File(outFolder, file.getName()+".debug.txt");
			String lyxBaseFile = new File(outFolder, FilenameUtils.getBaseName(file.getName())).getAbsolutePath();
			challenges = parseChallengeResponsePairs(file);
			List<ChallengeResponsePair> debugChallenges = new ArrayList<>(challenges);
			System.out.println("\tLoaded "+challenges.size()+" challenges.");
			if (pragma.isIncludeRerversed()||pragma.isOnlyRerversed()){
				List<ChallengeResponsePair> tmp=new ArrayList<>();
				challenges.forEach(pair->{
					ChallengeResponsePair newPair = new ChallengeResponsePair(pair);
					String t = newPair.challenge;
					newPair.challenge=newPair.response;
					newPair.response=t;
					tmp.add(newPair);
				});
				if (pragma.isOnlyRerversed()) {
					challenges.clear();
				}
				challenges.addAll(tmp);
			}			
			if (pragma.isSort()) {
				sortChallengeResponsePairsByLengthAlpha(challenges);
			}
			if (pragma.isRandom()) {
				int length = challenges.stream().mapToInt(c -> c.challenge.length()*2 + c.response.length()*3).sum();
				Random rnd = new Random(challenges.size() + length);
				Collections.shuffle(challenges, rnd);
			}
			queued = createPimsleurStyledOutput(challenges, pragma.getDepth());
			//space fix and puncation fix
			queued.forEach(q->{
				q.challenge=StringUtils.strip(StringUtils.normalizeSpace(q.challenge));
				q.response=StringUtils.strip(StringUtils.normalizeSpace(q.response));
				q.challenge=q.challenge.replaceAll("\\s+([.!?])", "$1");
				q.response=q.response.replaceAll("\\s+([.!?])", "$1");
			});
			if (pragma.isForPictures()) {
				List<Integer> numbers = new ArrayList<>();
				for (int i=0; i<queued.size(); i++) {
					numbers.add(i+1);
				}
				Random rnd = new Random(challenges.size() + numbers.size());
				Collections.shuffle(numbers, rnd);
				log.info("NUMBERS: "+numbers.toString());
				for (int i=0; i<queued.size(); i++) {
					ChallengeResponsePair q=queued.get(i);
					if (!StringUtils.isBlank(q.response)){
						q.response+=q.sep+" ";
					}
					q.response+=("["+numbers.get(i)+"]");
				}
			}
			writeDebugResponsePairsTxt(debugOutFile, debugChallenges);
			writeChallengeResponsePairsTxt(outFile, queued);
			writeChallengeResponsePairsLyx(lyxBaseFile, queued);
		}
	}

	private void writeDebugResponsePairsTxt(File outFile, List<ChallengeResponsePair> queued) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (ChallengeResponsePair c: queued) {
			sb.append(c.challenge.replace("\t", "\\t").replace("\n", "\\n"));
			sb.append("\n");
			sb.append(c.response.replace("\t", "\\t").replace("\n", "\\n"));
			sb.append("\n");
			sb.append("\n");
		}
		FileUtils.write(outFile, sb.toString(), StandardCharsets.UTF_8);		
	}

	private void writeChallengeResponsePairsLyx(String lyxBaseFile, List<ChallengeResponsePair> queued)
			throws IOException {
		
		System.out.println("Queued: "+queued.size());
		double sets = Math.ceil((float) queued.size() / pragma.getMaxSetSize());
		System.out.println("\tSets: "+sets);
		int countPerSet = (int)Math.ceil((double)queued.size() / sets);
		System.out.println("\tPer set: "+countPerSet);
		List<List<ChallengeResponsePair>> lists = ListUtils.partition(queued,countPerSet);

		StringBuilder lyx_challenges_only = new StringBuilder();
		StringBuilder lyx_challenges_response = new StringBuilder();

		if (pragma.getMaxsets()>0) {
			lists=lists.subList(0, Math.min(lists.size(), pragma.getMaxsets()));
		}
		
		lyx_challenges_only.append(LyxTemplate.docStart);
		lyx_challenges_response.append(LyxTemplate.docStart);

		int section = 1;
		for (List<ChallengeResponsePair> list : lists) {
			lyx_challenges_only.append(LyxTemplate.subsubsection(section));
			lyx_challenges_response.append(LyxTemplate.subsubsection(section));
			lyx_challenges_only.append(LyxTemplate.multiCol2_begin);
			lyx_challenges_response.append(LyxTemplate.multiCol2_begin);
			for (ChallengeResponsePair pair : list) {
				lyx_challenges_only.append(pair.toLyxCode(ResponseLayout.None));
				lyx_challenges_response.append(pair.toLyxCode(pragma.getLayout()));
			}
			lyx_challenges_only.append(LyxTemplate.multiCol2_end);
			lyx_challenges_response.append(LyxTemplate.multiCol2_end);
			section++;
		}

		lyx_challenges_only.append(LyxTemplate.docEnd);
		lyx_challenges_response.append(LyxTemplate.docEnd);

		FileUtils.write(new File(lyxBaseFile + "-co.lyx"), lyx_challenges_only.toString(), "UTF-8");
		FileUtils.write(new File(lyxBaseFile + "-cr.lyx"), lyx_challenges_response.toString(), "UTF-8");
	}

	private void sortChallengeResponsePairsByLengthAlpha(List<ChallengeResponsePair> challenges) {
		Collections.sort(challenges, (a, b) -> {
			if (a.challenge.length() != b.challenge.length()) {
				return a.challenge.length() - b.challenge.length();
			}
			return a.challenge.compareTo(b.challenge);
		});
	}

	public static class ReplacementSet {
		public String field;
		public String[] replacements;
		public List<String> deck = new ArrayList<>();
	}

	private List<ChallengeResponsePair> parseChallengeResponsePairs(File file) throws IOException {
		LineIterator ifile = FileUtils.lineIterator(file);
		List<ChallengeResponsePair> list = new ArrayList<>();
		while (ifile.hasNext()) {
			String line = ifile.next();
			if (StringUtils.isBlank(line)) {
				continue;
			}
			if (StringUtils.strip(line).startsWith("#pragma:")) {
				if (line.contains("forpictures")){
					pragma.setForPictures(true);
				}
				if (line.contains("include-reversed")){
					pragma.setIncludeRerversed(true);
				}
				if (line.contains("only-reversed")){
					pragma.setOnlyRerversed(true);
				}
				if (line.contains("layout=")) {
					String tmp = StringUtils.substringAfter(line, "layout=");
					tmp=StringUtils.strip(tmp);
					tmp=StringUtils.substringBefore(tmp, " ");
					tmp = StringUtils.strip(tmp);
					pragma.setLayout(ResponseLayout.valueOf(tmp));
				}
				if (line.contains("sep=")) {
					String tmp = StringUtils.substringAfter(line, "sep=");
					pragma.setSep(StringUtils.strip(StringUtils.substringBefore(tmp, " ")));
				}
				if (line.contains("nosort")) {
					pragma.setSort(false);
				}
				if (line.contains("random")) {
					pragma.setRandom(true);
					pragma.setSort(false);
					pragma.setDepth(1);
				}
				// maxSetSize
				if (line.contains("setsize=")) {
					String tmp = StringUtils.substringAfter(line, "setsize=");
					tmp = StringUtils.substringBefore(tmp, " ");
					try {
						pragma.setMaxSetSize(Integer.valueOf(tmp));
					} catch (NumberFormatException e) {
					}
				}
				if (line.contains("depth=")) {
					String tmp = StringUtils.substringAfter(line, "depth=");
					tmp = StringUtils.substringBefore(tmp, " ");
					try {
						pragma.setDepth(Integer.valueOf(tmp));
					} catch (NumberFormatException e) {
					}
				}
				if (line.contains("maxsets=")) {
					String tmp = StringUtils.substringAfter(line, "maxsets=");
					tmp = StringUtils.substringBefore(tmp, " ");
					try {
						pragma.setMaxsets(Integer.valueOf(tmp));
					} catch (NumberFormatException e) {
					}
				}
				continue;
			}
			if (StringUtils.strip(line).startsWith("#random:")) {
				String tmp = StringUtils.substringAfter(line, ":");
				String field = StringUtils.strip(StringUtils.substringBefore(tmp, "="));
				tmp = StringUtils.substringAfter(tmp, "=");
				String[] values = StringUtils.split(tmp, ",");
				ReplacementSet rset = new ReplacementSet();
				rset.field = field;
				rset.replacements = values;
				randomReplacements.put("<" + field + ">", rset);
				continue;
			}
			if (StringUtils.strip(line).startsWith("#")) {
				continue;
			}

			if (!line.contains("\t") && !pragma.isForPictures()) {
				System.err.println("BAD LINE (no tabs found): " + line);
				continue;
			}
			ChallengeResponsePair pair = new ChallengeResponsePair();
			String beforeTab = StringUtils.substringBefore(line, "\t");
			String afterTab = StringUtils.substringAfter(line, "\t");
			pair.challenge = StringUtils.strip(beforeTab);
			pair.response = StringUtils.strip(afterTab);
			pair.sep = pragma.getSep();
			list.add(pair);
		}
		return list;
	}

	/**
	 * base pimsleur timings (seconds): 1(5^0), 5(5^1), 25(5^2), 125(5^3),
	 * 625(5^4), ...
	 */
	private List<ChallengeResponsePair> createPimsleurStyledOutput(List<ChallengeResponsePair> challenges,
			int intervals) {
		TimingSlots used_timing_slots = new TimingSlots();
		List<ChallengeResponsePair> queued = new ArrayList<>();
		for (ChallengeResponsePair pair : challenges) {
			int seconds_offset = 0;
			for (int interval = 0; interval < intervals; interval++) {
				ChallengeResponsePair new_pair = new ChallengeResponsePair(pair);
				int seconds_start = (int) Math.pow(5, interval) + seconds_offset;
				int length = (int) Math.ceil(new_pair.challenge.length() / 5f);
				while (used_timing_slots.isUsed(seconds_start, length)) {
					seconds_start++;
				}
				used_timing_slots.markUsed(seconds_start, length);
				new_pair.position = seconds_start;
				seconds_offset = seconds_start;
				queued.add(new_pair);
			}
		}
		Collections.sort(queued, (a, b) -> a.position - b.position);
		int maxtries = 10;
		redorder_dupes: do {
			if (maxtries-- < 0) {
				break redorder_dupes;
			}
			Iterator<ChallengeResponsePair> iq = queued.iterator();
			ChallengeResponsePair prev = null;
			while (iq.hasNext()) {
				ChallengeResponsePair a = iq.next();
				if (a.equals(prev)) {
					iq.remove();
					queued.add(a);
					continue redorder_dupes;
				}
				prev = a;
			}
			break;
		} while (true);
		int length = challenges.stream().mapToInt(c -> c.challenge.length() + c.response.length()).sum();
		Random rnd = new Random(pragma.getDepth() + challenges.size() + length);
		for (ChallengeResponsePair new_pair : queued) {
			nextfield: for (String field : randomReplacements.keySet()) {
				String field_alt = "<=" + field.substring(1);
				if (!new_pair.challenge.contains(field) //
						&& !new_pair.response.contains(field) //
						&& !new_pair.challenge.contains(field_alt) //
						&& !new_pair.response.contains(field_alt)) {
					continue nextfield;
				}
				ReplacementSet rset = randomReplacements.get(field);
				if (rset.replacements.length == 0) {
					continue nextfield;
				}
				if (rset.deck.size() == 0) {
					rset.deck.addAll(Arrays.asList(rset.replacements));
					Collections.shuffle(rset.deck, rnd);
				}
				String tmp = rset.deck.remove(0);
				String a = StringUtils.substringBefore(tmp, "=");
				String b = StringUtils.substringAfter(tmp, "=");
				a = StringUtils.strip(a);
				b = StringUtils.strip(b);
				new_pair.challenge = new_pair.challenge.replace(field, a);
				new_pair.challenge = new_pair.challenge.replace(field_alt, b);
				new_pair.response = new_pair.response.replace(field, a);
				new_pair.response = new_pair.response.replace(field_alt, b);
			}
		}
		/**
		 * Remove back-to-back duplicates.
		 */
		Iterator<ChallengeResponsePair> iq = queued.iterator();
		ChallengeResponsePair prev = null;
		while (iq.hasNext()) {
			ChallengeResponsePair a = iq.next();
			if (a.equals(prev)) {
				iq.remove();
				continue;
			}
			prev = a;
		}
		return queued;
	}

	
	
	private void writeChallengeResponsePairsTxt(File outFile, List<ChallengeResponsePair> queued) throws IOException {
		FileUtils.writeLines(outFile, queued);
	}

	private void initFolders() {
		String baseDir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
		subdircheck: {
			if (new File(baseDir, "Documents").isDirectory()) {
				baseDir = baseDir + "/Documents";
				break subdircheck;
			}
			if (new File(baseDir, "My Documents").isDirectory()) {
				baseDir = baseDir + "/My Documents";
				break subdircheck;
			}
		}
		inFolder = baseDir + "/ᏣᎳᎩ/ExerciseInterleavedMaker/input";
		outFolder = baseDir + "/ᏣᎳᎩ/ExerciseInterleavedMaker/output";
		new File(inFolder).mkdirs();
		new File(outFolder).mkdirs();
	}

}
