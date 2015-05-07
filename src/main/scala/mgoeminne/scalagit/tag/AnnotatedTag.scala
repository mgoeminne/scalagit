package mgoeminne.scalagit.tag

import mgoeminne.scalagit.{Git, Commit}
import org.joda.time.DateTime

/**
 * A tag with full objects stored in the Git database.
 * It is checksummed, contains the tagger name, e-mail, and date;
 * it has a tagging message and can be signed and verified with GNU Privacy Guard (GPG).
 */
class AnnotatedTag(name: String, val tagger: String, val date: DateTime, commit: Commit, repository: Git) extends Tag(name, commit, repository)

