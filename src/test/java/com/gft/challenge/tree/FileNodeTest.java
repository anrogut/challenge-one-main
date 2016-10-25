package com.gft.challenge.tree;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileNodeTest {

    private static final String ROOT_PATH = "src";

    @Test
    public void shouldContainCorrectFiles() {
        FileNode root = new FileNode(new File(ROOT_PATH));
        Iterator<Node<File>> it = TreeDescendantsProvider.getDescendants(root);
        List<String> correctPathList = Arrays.asList(
                "src\\main\\java\\com\\gft\\challenge\\tree\\FileNode.java",
                "src\\test\\java\\com\\gft\\challenge\\tree\\FileNodeTest.java",
                "src\\main\\resources\\application.yml"
        );
        List<String> listOfPaths = new ArrayList<>();
        it.forEachRemaining((e) -> listOfPaths.add(e.get().getPath()));

        assertThat(listOfPaths).containsAll(correctPathList);
    }
}
