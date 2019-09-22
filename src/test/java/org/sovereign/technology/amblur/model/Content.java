package org.sovereign.technology.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Content implements Serializable {

	private static final long serialVersionUID = -6341957813996458039L;

	private String value;
}
