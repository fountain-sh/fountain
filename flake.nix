{
  description = "Fountain Toolchain";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=24.05";
  };

  outputs = { self, nixpkgs, flake-utils }: flake-utils.lib.eachDefaultSystem (system:
    let
      pkgs = import nixpkgs {
        inherit system;
      };
    in
    with pkgs;
    {
      devShells.default = mkShell {
        buildInputs = [ jdk17 gradle ];
      };
    }
  );
}
