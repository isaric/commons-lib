package com.path.variable.commons.slack;

import static com.path.variable.commons.slack.BlockType.*;

public class Block {

	private final BlockType type;

	private final Text text;

	public Block(BlockType type) {
		this.type = type;
		if (type == TEXT) this.text = new Text();
		else this.text = null;
	}

	private void validateIsTextBlock() {
		if (type != TEXT) {
			throw new UnsupportedOperationException("This block is not a text block!");
		}
	}

	public Text getText() {
		validateIsTextBlock();
		return text;
	}

	public BlockType getType() {
		return type;
	}
}
