# scalagit

[![Build Status](https://travis-ci.org/mgoeminne/scalagit.svg?branch=master)](https://travis-ci.org/mgoeminne/scalagit)
[![Coverage Status](https://coveralls.io/repos/mgoeminne/scalagit/badge.svg?branch=master&service=github)](https://coveralls.io/github/mgoeminne/scalagit?branch=master)

A simple binding for Git in Scala 


# Quickstart

```bash
$ git clone https://github.com/mgoeminne/scalagit.git ./scalagit
$ cd scalagit
$ sbt package
```

# Getting started

This library can be used for querying a local Git repository. Currently, only read operations are allowed as scala-git 
is mainly used for analysis purpose. 
A recent version of Git needs to be installed in order to benefit from the services proposed by the library.

## Git objects

The entry point of any analysis is the creation of an object representing the Git repository. This repository can be regular,
in which case the directory containing the .git directory must be used. Bare repositories are also exploitable by using 
the X.git directory itself.
 
```scala
val repository = new Git(new File("/foo/bar/repos.git"))
```

Once such an object has been create, several methods can be used to list the commits, files and contributors involved in
the repository.

```scala
import com.github.nscala_time.time.Imports._

val recent_commits = repository.commits.filter(c => c.date >= DateTime.now - 2.months)
val n_local_branches = repository.local_branches.size()
val committers = repository.commits.map(_.author).flatten
```

## Commit and Blob objects

A commit object represents a commit and contains the most common values associated to it. In addition, the content associated
to the commit can be obtained by invoking *Commit::tree*. The retrieved element is a tree, the nodes of which being either 
treenodes, either blobs. The blobs are elements representing the actual content of files existing in the considered commit.

The ancestors of the considered commit (if any), can be obtained by successively invoking the parents methods:

```scala
val commit = repository.commit.head
val is_merge_commit = commit.parents.size > 1
```

The list of all files associated to acommit, as well the blobs representing the content of these files, can also be directly
 accessed:
 
```scala
val commit = repository.commit.head
val file_blob = commit.existing_files
val files = commit.files
val blobs = commit.blobs
```

If the content stored in a blob is textual, its value can be retrieved as a character string that contains he potential 
new line characters.

```scala
val content = blob.lines 
```

## Reverse and transversal methods

The library also includes some methods that allow the user to break the Git storage schema. You can for instance determine
all the commits in which a given blob has been stored, or all the commits in which a file was present.

```scala
val f = blob.files.head
val commits = f.commits
```

These operations typically require a longer execution time, since the Git storage schema is not adapted for quickly answering 
the submitted queries, or because Git does not provide any tools for directly execute them, which forces scala-git to 
send multiple simpler queries to Git.


## How does it work?

Scala-git is essentially a thin set of helpers for executing the Git executable. In some cases, it also manipulates the obtained 
results for presenting them in a format that facilitates further manipulations. That means that the obtained results may depend 
on the version of Git you installed on your computer (the Git API sometimes subtly changes over time). 

It also means that Scala-git only exposes a subset of all the features provided by Git. In particular, no write operations 
are possible currently, and read operation mainly aims to provide a clear representation of a Git repository. Operations 
are added when I need them, so feel free to propose a patch for supporting your own required features.

Most operations retrieve stream of data, which allows you to only wait until Git retrieves the information you are interested in. 
Streams tend to minimize the overhead caused by the extra layer introduced by the library. However, no significant 
performance benchmark has been carried out so far for determining the importance of this overhead.


# Licence

LGPL 3