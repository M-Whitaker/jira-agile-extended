import ForgeUI, {
  render,
  useProductContext,
  CustomField,
  CustomFieldEdit,
  Text,
  Select,
  Option,
  Image,
  Link
} from "@forge/ui";

const View = () => {
  const {extensionContext: {fieldValue}} = useProductContext();

  return (
    <CustomField>
      <Image size={"xsmall"} src={"/viewavatar?size=medium&avatarId=10315&avatarType=issuetype"} alt={"issueType"}/>
      <Text>
        <Link href={"/browse/TEST-1"}>
          {fieldValue}
        </Link>
        Issue Summary
      </Text>
    </CustomField>
  );
};

const Edit = () => {
  const onSubmit = values => {
    return values.text;
  };

  return (
    <CustomFieldEdit onSubmit={onSubmit}>
      <Select label="Select Parent:" name="text">
        <Option defaultSelected label="TEST-1" value="TEST-1" />
        <Option label="TEST-2" value="TEST-2" />
        <Option label="TEST-3" value="TEST-3" />
      </Select>
    </CustomFieldEdit>
  );
}

export const runView = render(<View/>);
export const runEdit = render(<Edit/>);
