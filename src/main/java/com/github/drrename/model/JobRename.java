/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.drrename.model;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kerner1000.drrename.RenamingStrategy;
import com.github.drrename.ConfirmDialogFactory;

import net.sf.kerner.utils.concurrent.Pipe;

/**
 *
 * @author alex
 */
public class JobRename implements Callable<Void>, FileVisitor<Path> {

	public static interface Listener {

		void nextFile(String oldName, String newName);
	}

	private final List<Listener> listener = new ArrayList<>();
	private final RenamingStrategy renamingStrategy;
	private final String startDir;
	private final boolean recurseIntoSubDirectories;
	private int dirCnt = 0;
	private final AtomicBoolean yesAll = new AtomicBoolean(false);
	private List<String> directoryFilter = new ArrayList<>();
	private final Logger log = LoggerFactory.getLogger(JobRename.class);

	public JobRename(final RenamingStrategy renamingStrategy, final String startDir, final boolean recurseIntoSubDirectories) {

		this.renamingStrategy = renamingStrategy;
		this.startDir = startDir;
		this.recurseIntoSubDirectories = recurseIntoSubDirectories;
	}

	@Override
	public Void call() throws Exception {

		if(startDir == null)
			throw new IllegalArgumentException("Please specify starting directory.");
		try {
			readFilters();
		} catch(final IOException e) {
			if(log.isInfoEnabled())
				log.info(e.toString());
		}
		final Path result = Files.walkFileTree(Paths.get(startDir), getVisitingOptions(), Integer.MAX_VALUE, this);
		if(log.isDebugEnabled())
			log.debug("finished " + result);
		return null;
	}

	public List<String> getDirectoryFilter() {

		return directoryFilter;
	}

	public List<Listener> getListener() {

		return listener;
	}

	private Set<FileVisitOption> getVisitingOptions() {

		final Set<FileVisitOption> result = new LinkedHashSet<>();
		result.add(FileVisitOption.FOLLOW_LINKS);
		return result;
	}

	@Override
	public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {

		if(Thread.interrupted()) {
			if(log.isDebugEnabled())
				log.debug("Interrupted, skipping " + dir.getFileName());
			return FileVisitResult.TERMINATE;
		}
		for(final String filter : directoryFilter)
			if(dir.getFileName().toString().startsWith(filter)) {
				if(log.isDebugEnabled())
					log.debug(dir.getFileName() + " is filtered");
				return FileVisitResult.SKIP_SUBTREE;
			}
		if(recurseIntoSubDirectories || (dirCnt < 1)) {
			dirCnt++;
			return FileVisitResult.CONTINUE;
		}
		if(log.isDebugEnabled())
			log.debug("Skipping content of " + dir.getFileName());
		return FileVisitResult.SKIP_SUBTREE;
	}

	private void readFilters() throws IOException {

		final String content = new String(Files.readAllBytes(Paths.get("dfilter.txt")));
		final String[] filters = content.split("\n");
		directoryFilter = Arrays.asList(filters);
	}

	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

		try {
			final Pipe<Integer> exReturnval = new Pipe<>();
			final String result = renamingStrategy.getNameNew(file);
			if(result.equals(file.getFileName().toString()))
				// if (log.isDebugEnabled()) {
				// log.debug("Nothing to do here, skipping " + result);
				// }
				return FileVisitResult.CONTINUE;
			if(!yesAll.get())
				ConfirmDialogFactory.showDialog(file, result, exReturnval);
			if(yesAll.get()) {
				try {
					renamingStrategy.rename(file, attrs);
					for(final Listener l : listener)
						l.nextFile(file.getFileName().toString(), result);
				} catch(final IOException e) {
					if(log.isErrorEnabled())
						log.error(e.getLocalizedMessage(), e);
					return FileVisitResult.TERMINATE;
				} catch(final InterruptedException e) {
					if(log.isDebugEnabled())
						log.debug(e.toString());
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
			final int returnval = exReturnval.take();
			if(returnval == 0) {
				try {
					renamingStrategy.rename(file, attrs);
					for(final Listener l : listener)
						l.nextFile(file.getFileName().toString(), result);
				} catch(final IOException e) {
					if(log.isErrorEnabled())
						log.error(e.getLocalizedMessage(), e);
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
			// cancel
			if(returnval == 2)
				return FileVisitResult.TERMINATE;
			// no
			if(returnval == 1)
				return FileVisitResult.CONTINUE;
			// no
			if(returnval == 3) {
				yesAll.set(true);
				if(log.isDebugEnabled())
					log.debug("All yes, no more asking!");
				try {
					renamingStrategy.rename(file, attrs);
					for(final Listener l : listener)
						l.nextFile(file.getFileName().toString(), result);
				} catch(final IOException e) {
					if(log.isErrorEnabled())
						log.error(e.getLocalizedMessage(), e);
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
			if(log.isWarnEnabled())
				log.warn("Unknown return value " + returnval);
		} catch(final InterruptedException e1) {
			if(log.isInfoEnabled())
				log.info(e1.toString());
			return FileVisitResult.TERMINATE;
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {

		if(log.isErrorEnabled())
			log.error(exc.getLocalizedMessage(), exc);
		return FileVisitResult.CONTINUE;
	}
}
