package com.cherokeelessons.eim;

public class ChallengeResponsePair {
	public ChallengeResponsePair() {
	}

	public ChallengeResponsePair(ChallengeResponsePair pair) {
		position = pair.position;
		challenge = pair.challenge;
		response = pair.response;
		sep = pair.sep;
	}

	public int position;
	public String challenge;
	public String response;
	public String sep=",";

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String tmp_challenge = challenge != null ? challenge : "";
		sb.append(tmp_challenge);
		sb.append("\t");
		String tmp_response = response != null ? response : "";
		sb.append(tmp_response);
		sb.append("\t");
		sb.append(tmp_challenge);
		sb.append(sep);
		sb.append(" ");
		sb.append(tmp_response);
		sb.append("\t");
		sb.append(position);
		return sb.toString();
	}

	public static enum ResponseLayout {
		None, Enumerate, Plain, Itemize, SingleLine
	}

	public String toLyxCode(ResponseLayout challengesOnly) {
		StringBuilder sb = new StringBuilder();
		String tmp_challenge = challenge != null ? challenge : "";
		String tmp_response = response != null ? response : "";

		sb.append("\\begin_layout Enumerate\n");
		sb.append(tmp_challenge);
		
		switch(challengesOnly) {
		case SingleLine:
			sb.append("\n"+sep+"\n ");
			sb.append(tmp_response);
			sb.append("\n\\end_layout\n\n");
			break;
		case Enumerate:
			sb.append("\n\\end_layout\n\n");
			sb.append("\\begin_deeper\n");
			for (String t: tmp_response.split("\t")) {
				sb.append("\\begin_layout Enumerate\n");
				sb.append(t);
				sb.append("\n\\end_layout\n\n");
			}
			sb.append("\\end_deeper\n");
			break;
		case Itemize:
			sb.append("\n\\end_layout\n\n");
			sb.append("\\begin_deeper\n");
			for (String t: tmp_response.split("\t")) {
				sb.append("\\begin_layout Itemize\n");
				sb.append(t);
				sb.append("\n\\end_layout\n\n");
			}
			sb.append("\\end_deeper\n");
			break;
		case None:
			sb.append("\n\\end_layout\n\n");
			break;
		case Plain:
			sb.append("\n\\end_layout\n\n");
			sb.append("\\begin_deeper\n");
			sb.append("\\begin_layout Standard\n");
			sb.append(tmp_response);
			sb.append("\n\\end_layout\n\n");
			sb.append("\\end_deeper\n");
			break;
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((challenge == null) ? 0 : challenge.hashCode());
		result = prime * result + ((response == null) ? 0 : response.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChallengeResponsePair other = (ChallengeResponsePair) obj;
		if (challenge == null) {
			if (other.challenge != null) {
				return false;
			}
		} else if (!challenge.equals(other.challenge)) {
			return false;
		}
		if (response == null) {
			if (other.response != null) {
				return false;
			}
		} else if (!response.equals(other.response)) {
			return false;
		}
		return true;
	}

}