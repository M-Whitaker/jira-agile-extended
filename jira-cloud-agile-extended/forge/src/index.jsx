import ForgeUI, {
  CustomField,
  CustomFieldEdit,
  Image,
  Link,
  Option,
  render,
  Select,
  Text,
  useProductContext,
  useState
} from "@forge/ui";
import api, {route} from "@forge/api";

const View = () => {
  const {extensionContext: {fieldValue}} = useProductContext();
  const issue = useState(async () => await getIssue(fieldValue));
  console.log(issue[0].fields);

  return (
    <CustomField>
      <Image size={"xsmall"} src={issue[0].fields.issuetype.iconUrl} alt={"issueType"}/>
      <Text>
        <Link href={route`/browse/{fieldValue}`}>
          {fieldValue}
        </Link>
        {issue[0].fields.summary}
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

const getIssue = async (issueId) => {
  const res = await api
      .asUser()
      .requestJira(route`/rest/api/3/issue/${issueId}`);

  return await res.json();
};

export const runView = render(<View/>);
export const runEdit = render(<Edit/>);
