package io.github.brandonjmitchell.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address implements Serializable {

	private static final long serialVersionUID = -7829509751386865770L;

	private String street;
	private String city;
	private String state;
	private String postalCode;
}
