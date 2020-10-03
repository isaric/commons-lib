package com.path.variable.commons.slack;

import java.util.List;
import java.util.ArrayList;


public class SlackMessage {

	private List<Block> blocks;

	public SlackMessage() {
		this.blocks = new ArrayList<>();
	}

	public Block addSection() {
		var block = new Block(BlockType.TEXT);
		blocks.add(block);
		return block;
	}

	private Block addDivider() {
		var block = new Block(BlockType.DIVIDER);
		blocks.add(block);
		return block;
	}

}
