package com.gft.challenge.tree;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathNodeTest {

    private static final String ROOT_PATH = "src";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public void shouldContainCorrectFiles() {
        Node<Path> root = new PathNode(Paths.get(ROOT_PATH));
        Iterator<Node<Path>> it = TreeDescendantsProvider.getDescendants(root);
        List<String> correctPathList = Arrays.asList(
                "src\\main\\java\\com\\gft\\challenge\\tree\\PathNode.java",
                "src\\test\\java\\com\\gft\\challenge\\tree\\PathNodeTest.java",
                "src\\main\\resources\\application.yml"
        );
        List<String> listOfPaths = new ArrayList<>();
        it.forEachRemaining((e) -> listOfPaths.add(e.get().toString()));

        assertThat(listOfPaths).containsAll(correctPathList);
    }

    @Test
    public void shouldContainExactlyTwoFiles() throws IOException {
        tempFolder.newFolder("one", "two");

        assertThat(TreeDescendantsProvider.getDescendants(new PathNode(tempFolder.getRoot().toPath()))).hasSize(2);
    }
}
