/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.mavenplugins;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * Goal which runs profiling regression tests.
 *
 * @goal profiler
 *
 * @phase test
 */
public final class ProfilerMojo extends AbstractMojo {
	/**
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	private ArtifactRepository localRepository;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	private  List<ArtifactRepository> remoteRepositories;
	
	/**
	 * @parameter baselineVersion
	 * @required
	 */
	private String baselineVersion;

	/**
	 * The Maven Project Object.
	 *
	 * @parameter default-value="${project}"
	 * @readonly
	 */
	private MavenProject project;

	/** @component */
	private ArtifactResolver resolver;

	/** @component */
	private ArtifactFactory artifactFactory;
	
	public void execute() throws MojoExecutionException {
		getLog().info("+++ ProfilerMojo: base line version is " + baselineVersion + ", group ID: "
			      + project.getGroupId() + ", artifact ID: " + project.getArtifactId());
		final List<String> testClasspath = generateTestClasspath();
		getLog().info("+++ ProfilerMojo: testClasspath = " + testClasspath);
		final Artifact artifact = project.getArtifact();
		resolveDependencies(artifact);
		collectStats(artifact);
		final Artifact baselineArtifact = artifactFactory.createArtifactWithClassifier(artifact.getGroupId(),
											       artifact.getArtifactId(),
											       baselineVersion,
											       artifact.getType(),
											       artifact.getClassifier());
		resolveDependencies(baselineArtifact);
		collectStats(baselineArtifact);
		compareStats();
	}

	private List<String> generateTestClasspath() {
		final List<String> testClasspath = new ArrayList<String>();
		for (final Object a : project.getArtifacts()) {
			final Artifact artifact = (Artifact)a;
			if (artifact.getArtifactHandler().isAddedToClasspath()) {
				final File file = artifact.getFile();
				if (file != null)
					testClasspath.add(file.getPath());
			}
		}

		return testClasspath;
	}

	private void collectStats(final Artifact artifact) {
		System.gc();
	}

	private void compareStats() {
	}

	private void resolveDependencies(final Artifact artifact) throws MojoExecutionException {
		try {
			resolver.resolve(artifact, remoteRepositories, localRepository);
		} catch (final ArtifactResolutionException are) {
			throw new MojoExecutionException("Failed to resolve an artifact!", are);
		} catch (final ArtifactNotFoundException anfe) {
			throw new MojoExecutionException("Failed to find an artifact!", anfe);
		}
		getLog().info("+++ ProfilerMojo: resolved " + artifact);
	}
}
