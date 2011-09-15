/** \file    NGramUtil.cc
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Jiangtao Hu
 *  \brief   Implementation of text related utility functions.
 */

/*
 *  Copyright 2003-2009 Project iVia.
 *  Copyright 2003-2009 The Regents of The University of California.
 *
 *  This file is part of the libiViaCore package.
 *
 *  The libiViaCore package is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  libiViaCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with libiViaCore; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <NGramUtil.h>
#include <Compiler.h>
#include <Directory.h>
#include <File.h>
#include <MiscUtil.h>
#include <SList.h>
#include <StringUtil.h>


#define DIM(array)	(sizeof(array) / sizeof(array[0]))


namespace {


std::string GetShareDir() {
#ifdef __MACH__
	return MiscUtil::GetEnv("HOME") + "/share";
#else
	return SHARE_DIR;
#endif
}


/** The default location of the models used by ClassifyLanguage. */
const std::string DEFAULT_LANGUAGE_MODELS_DIRECTORY = GetShareDir() + "/language_models";


#if 0
std::ostream &operator<<(std::ostream &stream, const  &ngram_counts)
{
	for (NGramUtil::NGramCounts::const_iterator pair(ngram_counts.begin()); pair != ngram_counts.end(); ++pair)
		stream << pair->first << '\t' << pair->second <<'\n';

	return stream;
}
#endif


void CountWords(const std::string &s, const std::string &non_word_chars, GNU_HASH_MAP<std::string, unsigned> * const words_and_counts)
{
	bool in_word(false);
	std::string new_word;
	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		if (non_word_chars.find(*ch) != std::string::npos) {
			if (in_word) {
				GNU_HASH_MAP<std::string, unsigned>::iterator word_and_count(words_and_counts->find(new_word));
				if (word_and_count == words_and_counts->end())
					words_and_counts->insert(std::make_pair<std::string, unsigned>(new_word, 1));
				else
					++word_and_count->second;
				in_word = false;
			}
		}
		else if (in_word)
			new_word += *ch;
		else {
			in_word = true;
			new_word = *ch;
		}
	}

	if (in_word) {
		GNU_HASH_MAP<std::string, unsigned>::iterator word_and_count(words_and_counts->find(new_word));
		if (word_and_count == words_and_counts->end())
			words_and_counts->insert(std::make_pair<std::string, unsigned>(new_word, 1));
		else
			++word_and_count->second;
	}
}


// LoadModel -- loads an n-gram model from "path_name" into "ngram_counts".
//
void LoadModel(const std::string &path_name, GNU_HASH_MAP<std::string, double> * const ngrams_and_rel_frequencies)
{
	File input(path_name, "r");
	if (input.fail())
		throw Exception("in LoadModel(NGramUtil.cc): can't open model file \"" + path_name + "\" for reading!");

	std::string non_ngram_chars("1234567890");
	const std::set<char> &white_space_chars(MiscUtil::GetWhiteSpaceSet());
	for (std::set<char>::const_iterator white_space_char(white_space_chars.begin()); white_space_char != white_space_chars.end(); ++white_space_char)
		non_ngram_chars += *white_space_char;

	while (not input.eof()) {
		std::string line;
		input.getline(&line);

		std::vector<std::string> components;
		double rel_frequency;
		if (unlikely(StringUtil::WhiteSpaceSplit(line, &components) != 2) or not StringUtil::ToDouble(components[1], &rel_frequency))
			return;

		(*ngrams_and_rel_frequencies)[components[0]] = rel_frequency;
	}
}


// LoadModels -- returns true if at least one language model was loaded from "language_models_directory"
//
bool LoadModels(const std::string &language_models_directory, const unsigned topmost_use_count, SList<NGramUtil::Model> * const language_models)
{
	language_models->clear();

	Directory directory(language_models_directory, "*.lm");
	bool found_at_least_one_language_model(false);
	for (Directory::const_iterator dir_entry(directory.begin()); dir_entry != directory.end(); ++dir_entry) {
		GNU_HASH_MAP<std::string, double> ngrams_and_rel_frequencies;
		LoadModel(language_models_directory + "/" + dir_entry->getFileOrDirectoryName(), &ngrams_and_rel_frequencies);

		const std::string language(dir_entry->getFileOrDirectoryName().substr(0, dir_entry->getFileOrDirectoryName().length() - 3));
		language_models->push_back(NGramUtil::Model(language, ngrams_and_rel_frequencies, topmost_use_count));
		found_at_least_one_language_model = true;
	}

	return not language_models->empty();
}


} // unnamed namespace


namespace NGramUtil {


SortedStringValuePairs::SortedStringValuePairs(const GNU_HASH_MAP<std::string, double> &strings_and_values, const SortOrder sort_order)
{
	resize(strings_and_values.size());
	unsigned i = 0;
	for (GNU_HASH_MAP<std::string, double>::const_iterator entry(strings_and_values.begin()); entry != strings_and_values.end(); ++entry, ++i)
		(*this)[i] = StringValuePair(entry->first, entry->second);
	bool (*sort_func)(const StringValuePair &lhs, const StringValuePair &rhs);
	if (sort_order == ASCENDING_ORDER)
		sort_func = IsLessThan;
	else
		sort_func = IsGreaterThan;
	std::sort(pairs_.begin(), pairs_.end(), sort_func);
}


bool SortedStringValuePairs::IsLessThan(const StringValuePair &lhs, const StringValuePair &rhs)
{
	if (lhs.value_ == rhs.value_)
		return lhs.string_ > rhs.string_ ;
	return lhs.value_ < rhs.value_;
}


bool SortedStringValuePairs::IsGreaterThan(const StringValuePair &lhs, const StringValuePair &rhs)
{
	if (lhs.value_ == rhs.value_)
		return lhs.string_ < rhs.string_ ;
	return lhs.value_ > rhs.value_;
}


void IndexAndRelFrequency::serializeAndDeserialize(File &file, Serialization::Direction direction) const
{
	Serialization::Serialize(file, direction, index_);
	Serialization::Serialize(file, direction, rel_frequency_);
}


namespace {


inline bool RelFrequencyCompare(const std::pair<std::string, double> &string_and_rel_frequency1,
				const std::pair<std::string, double> &string_and_rel_frequency2)
{
	return string_and_rel_frequency1.second > string_and_rel_frequency2.second;
}


} // unnamed namespace


Model::Model(const std::string &model_name, const GNU_HASH_MAP<std::string, double> &ngrams_and_rel_frequencies, const unsigned topmost_use_count)
	: model_name_(model_name), max_simple_distance_(topmost_use_count), max_weighted_distance_(0.0)
{
	std::vector< std::pair<std::string, double> > strings_and_rel_frequencies;
	strings_and_rel_frequencies.reserve(ngrams_and_rel_frequencies.size());
	for (GNU_HASH_MAP<std::string, double>::const_iterator ngram_and_rel_frequency(ngrams_and_rel_frequencies.begin());
	     ngram_and_rel_frequency != ngrams_and_rel_frequencies.end(); ++ngram_and_rel_frequency)
		strings_and_rel_frequencies.push_back(*ngram_and_rel_frequency);
	std::sort(strings_and_rel_frequencies.begin(), strings_and_rel_frequencies.end(), RelFrequencyCompare);

	unsigned rank(1), count(0);
	double last_rel_frequency(strings_and_rel_frequencies.begin()->second);
	for (std::vector< std::pair<std::string, double> >::const_iterator ngram_and_rel_frequency(strings_and_rel_frequencies.begin());
	     count <= topmost_use_count and ngram_and_rel_frequency != strings_and_rel_frequencies.end(); ++ngram_and_rel_frequency, ++count)
	{
		if (ngram_and_rel_frequency->second < last_rel_frequency) {
			++rank;
			last_rel_frequency = ngram_and_rel_frequency->second;
		}

		key_to_index_and_rel_frequency_map_.insert(
			std::make_pair<std::string, IndexAndRelFrequency>(ngram_and_rel_frequency->first,
									  IndexAndRelFrequency(rank, ngram_and_rel_frequency->second)));
		max_weighted_distance_ += ngram_and_rel_frequency->second;
	}
}


double Model::distance(const Model &other_model, const DistanceType distance_type) const
{
	// Optimisation for short texts:
	if (other_model.size() < size())
		return other_model.distance(*this, distance_type);

	double total_distance(0.0);
	for (Model::const_iterator ngram_and_index_and_rel_frequency(key_to_index_and_rel_frequency_map_.begin());
	     ngram_and_index_and_rel_frequency != key_to_index_and_rel_frequency_map_.end(); ++ngram_and_index_and_rel_frequency)
		total_distance += other_model.distance(ngram_and_index_and_rel_frequency->first, ngram_and_index_and_rel_frequency->second.index_,
						       distance_type);

	return total_distance;
}


void Model::swap(Model &other)
{
	std::swap(model_name_, other.model_name_);
	std::swap(max_simple_distance_, other.max_simple_distance_);
	std::swap(max_weighted_distance_, other.max_weighted_distance_);
	std::swap(key_to_index_and_rel_frequency_map_, other.key_to_index_and_rel_frequency_map_);
}


void Model::serialize(const std::string &filename) const
{
	File output(filename, "w");
	if (unlikely(output.fail()))
		throw Exception("in NGramUtil::Model::serialize: can't open \"" + filename + "\" for writing!");

	serialize(output);
}


double Model::distance(const std::string &ngram, const int rank, const DistanceType distance_type) const
{
	const_iterator iter(key_to_index_and_rel_frequency_map_.find(ngram));
	if (iter == key_to_index_and_rel_frequency_map_.end())
		return (distance_type == SIMPLE_DISTANCE) ? max_simple_distance_ : max_weighted_distance_;
	else if (distance_type == WEIGHTED_DISTANCE) {
		double total_distance(0.0);
		for (int i(std::min(rank, iter->second.index_)); i < std::max(rank, iter->second.index_); ++i)
			total_distance += iter->second.rel_frequency_;
		return total_distance;
	}
	else
		return std::abs(iter->second.index_ - rank);
}


void Model::serializeAndDeserialize(File &file, Serialization::Direction direction) const
{
	Serialization::Serialize(file, direction, model_name_);
	Serialization::Serialize(file, direction, max_simple_distance_);
	Serialization::Serialize(file, direction, max_weighted_distance_);
	Serialization::Serialize(file, direction, key_to_index_and_rel_frequency_map_);
}


void CreateModel(const GNU_HASH_MAP<std::string, unsigned> &words_and_counts, const std::string &model_name, Model * const model,
		 const unsigned ngram_number_threshold, const unsigned topmost_use_count)
{
	unsigned total_ngram_count(0);
	GNU_HASH_MAP<std::string, double> ngrams_and_rel_frequencies;
	for (GNU_HASH_MAP<std::string, unsigned>::const_iterator word_and_count(words_and_counts.begin()); word_and_count != words_and_counts.end();
	     ++word_and_count)
	{
		const std::string funny_word("_" + word_and_count->first + "_");
		const std::string::size_type funny_word_length(funny_word.length());
		std::string::size_type length(funny_word_length);
		for (unsigned i = 0; i < funny_word_length; ++i, --length) {
			if (length > 4) {
				const std::string ngram(funny_word.substr(i, 5));
				const GNU_HASH_MAP<std::string, double>::iterator ngram_count(ngrams_and_rel_frequencies.find(ngram));
				if (ngram_count == ngrams_and_rel_frequencies.end())
					ngrams_and_rel_frequencies[ngram] = word_and_count->second;
				else
					ngrams_and_rel_frequencies[ngram] += word_and_count->second;
				++total_ngram_count;
			}

			if (length > 3) {
				const std::string ngram(funny_word.substr(i, 4));
				const GNU_HASH_MAP<std::string, double>::iterator ngram_count(ngrams_and_rel_frequencies.find(ngram));
				if (ngram_count == ngrams_and_rel_frequencies.end())
					ngrams_and_rel_frequencies[ngram] = word_and_count->second;
				else
					ngrams_and_rel_frequencies[ngram] += word_and_count->second;
				++total_ngram_count;
			}

			if (length > 2) {
				const std::string ngram(funny_word.substr(i, 3));
				const GNU_HASH_MAP<std::string, double>::iterator ngram_count(ngrams_and_rel_frequencies.find(ngram));
				if (ngram_count == ngrams_and_rel_frequencies.end())
					ngrams_and_rel_frequencies[ngram] = word_and_count->second;
				else
					ngrams_and_rel_frequencies[ngram] += word_and_count->second;
				++total_ngram_count;
			}

			if (length > 1) {
				const std::string ngram(funny_word.substr(i, 2));
				const GNU_HASH_MAP<std::string, double>::iterator ngram_count(ngrams_and_rel_frequencies.find(ngram));
				if (ngram_count == ngrams_and_rel_frequencies.end())
					ngrams_and_rel_frequencies[ngram] = word_and_count->second;
				else
					ngrams_and_rel_frequencies[ngram] += word_and_count->second;
				++total_ngram_count;
			}

			const std::string ngram(funny_word.substr(i, 1));
			const GNU_HASH_MAP<std::string, double>::iterator ngram_count(ngrams_and_rel_frequencies.find(ngram));
			if (ngram_count == ngrams_and_rel_frequencies.end())
				ngrams_and_rel_frequencies[ngram] = word_and_count->second;
			else
				ngrams_and_rel_frequencies[ngram] += word_and_count->second;
			++total_ngram_count;
		}
	}

	// Normalise the n-gram frequencies:
	for (GNU_HASH_MAP<std::string, double>::iterator ngram_count(ngrams_and_rel_frequencies.begin()); ngram_count != ngrams_and_rel_frequencies.end();
	     ++ngram_count)
		ngram_count->second /= total_ngram_count;

	// Remove entries that occur less than "ngram_number_threshold" times:
	for (GNU_HASH_MAP<std::string, double>::iterator entry(ngrams_and_rel_frequencies.begin()); entry != ngrams_and_rel_frequencies.end(); ++entry)
		if (entry->second < ngram_number_threshold)
			ngrams_and_rel_frequencies.erase(entry->first);

	Model new_model(model_name, ngrams_and_rel_frequencies, topmost_use_count);
	model->swap(new_model);
}


void CreateModel(std::istream &input, const std::string &model_name, Model * const model, const unsigned ngram_number_threshold,
		 const unsigned topmost_use_count)
{
	std::string file_contents;
	FileUtil::ReadFile(input, &file_contents);

	// Replace anything but letters and quotes with spaces.  "xlate_map" is used to convert all non-letters with the exception of single quotes
	// into spaces:
	static char xlate_map_table[256];
	static char * const xlate_map(xlate_map_table - CHAR_MIN);
	static bool initialized_xlate_map(false);
	if (not initialized_xlate_map) {
		initialized_xlate_map = true;

		for (int c = CHAR_MIN; c <= CHAR_MAX; ++c)
			xlate_map[c] = isalpha(c) ? c : ' ';
		xlate_map['\''] = '\'';
	}

	for (std::string::iterator ch(file_contents.begin()); ch != file_contents.end(); ++ch)
		*ch = static_cast<char>(xlate_map[static_cast<unsigned char>(*ch)]);

	static std::string whitespace;
	static bool whitespace_is_initialized(false);
	if (not whitespace_is_initialized) {
		whitespace_is_initialized = true;
		const std::set<char> &white_space_chars(MiscUtil::GetWhiteSpaceSet());
		for (std::set<char>::const_iterator white_space_char(white_space_chars.begin()); white_space_char != white_space_chars.end();
		     ++white_space_char)
			whitespace += *white_space_char;
	}

	GNU_HASH_MAP<std::string, unsigned> words_and_counts;
	CountWords(file_contents, whitespace, &words_and_counts);

	CreateModel(words_and_counts, model_name, model, ngram_number_threshold , topmost_use_count);
}


void ClassifyLanguage(std::istream &input, std::list<std::string> * const top_languages, const DistanceType distance_type,
		      const unsigned ngram_number_threshold, const unsigned topmost_use_count, const double alternative_cutoff_factor,
		      const std::string &override_language_models_directory)
{
	// Determine the language models directroy:
	const std::string language_models_directory(override_language_models_directory.empty() ? DEFAULT_LANGUAGE_MODELS_DIRECTORY
						                                               : override_language_models_directory);

	Model unknown_language_model;
	CreateModel(input, "unknown", &unknown_language_model, ngram_number_threshold, topmost_use_count);

	static bool models_already_loaded(false);
	static SList<Model> known_language_models;
	if (not models_already_loaded) {
		models_already_loaded = true;
		if (not LoadModels(language_models_directory, topmost_use_count, &known_language_models))
			MsgUtil::Error("no language models available in \"%s\"!", language_models_directory.c_str());
	}

	GNU_HASH_MAP<std::string, double> results;
	for (SList<Model>::const_iterator known_language_model(known_language_models.begin()); known_language_model != known_language_models.end();
	     ++known_language_model)
	{
		// Compare the known language model with the unknown language model:
		const double distance(known_language_model->distance(unknown_language_model, distance_type));
		results[known_language_model->getModelName()] = distance;
	}

	SortedStringValuePairs sorted_results(results, SortedStringValuePairs::ASCENDING_ORDER);

	// Select the top scoring language and anything that's close
	// (as defined by alternative_cutoff_factor):
	const double high_score = sorted_results[0].value_;
	top_languages->push_back(sorted_results[0].string_);
	for (unsigned i = 1; i < sorted_results.size() and (sorted_results[i].value_ < alternative_cutoff_factor * high_score); ++i)
		top_languages->push_back(sorted_results[i].string_);
}


}// namespace NGramUtil
